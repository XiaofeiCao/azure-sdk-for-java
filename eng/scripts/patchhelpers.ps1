# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.

$RepoRoot = Resolve-Path "${PSScriptRoot}../../.."
$CommonScriptFilePath = Join-Path $RepoRoot "eng" "common" "scripts" "common.ps1"
$BomHelpersFilePath = Join-Path $PSScriptRoot "bomhelpers.ps1"
$PatchReportFile = Join-Path $PSScriptRoot "patchreport.json"
$BomFilePath = Join-Path $RepoRoot "sdk" "boms" "azure-sdk-bom" "pom.xml"
$BomChangeLogPath = Join-Path $RepoRoot "sdk" "boms" "azure-sdk-bom" "changelog.md"
. $CommonScriptFilePath
. $BomHelpersFilePath

class ArtifactInfo {
    [string]$GroupId = "com.azure"
    [string]$ArtifactId
    [string]$ServiceDirectoryName
    [string]$ArtifactDirPath
    [string]$LatestGAOrPatchVersion
    [string]$FutureReleasePatchVersion
    [string]$CurrentPomFileVersion
    [string]$ChangeLogPath
    [string]$ReadMePath
    [string]$PipelineName
    [hashtable]$Dependencies

    ArtifactInfo([string]$ArtifactId, [string]$GroupId, [string]$LatestGAOrPatchVersion) {
        $this.ArtifactId = $ArtifactId
        $this.GroupId = $GroupId
        $this.LatestGAOrPatchVersion = $LatestGAOrPatchVersion
    }
}

function ConvertToPatchInfo([ArtifactInfo]$ArInfo) {
    $patchInfo = [ArtifactPatchInfo]::new()
    $patchInfo.ArtifactId = $ArInfo.ArtifactId
    $patchInfo.ServiceDirectoryName = $ArInfo.ServiceDirectoryName
    $patchInfo.ArtifactDirPath = $ArInfo.ArtifactDirPath
    $patchInfo.LatestGAOrPatchVersion = $ArInfo.LatestGAOrPatchVersion
    $patchInfo.CurrentPomFileVersion = $ArInfo.CurrentPomFileVersion
    $patchInfo.ChangeLogPath = $ArInfo.ChangeLogPath
    $patchInfo.ReadMePath = $ArInfo.ReadMePath
    $patchInfo.PipelineName = $ArInfo.PipelineName
    $patchInfo.FutureReleasePatchVersion = $arInfo.FutureReleasePatchVersion

    return $patchInfo
}

# Get version info for all the maven artifacts under the groupId = 'com.azure'
function GetVersionInfoForAllMavenArtifacts([string]$GroupId = "com.azure") {
    $artifactInfos = @{}
    $azComArtifactIds = GetAllAzComClientArtifactIds -GroupId $GroupId

    foreach ($artifactId in $azComArtifactIds) {
        $artifactInfos[$artifactId] = GetVersionInfoForMavenArtifact -ArtifactId $artifactId -GroupId $GroupId
    }

    return $artifactInfos
}

# Get version info for all a Maven artifact
function GetVersionInfoForMavenArtifact($ArtifactId, $GroupId = "com.azure") {
    $info = GetVersionInfoForAnArtifactId -GroupId $groupId -ArtifactId $artifactId
    $artifactId = $info.ArtifactId
    $latestGAOrPatchVersion = $info.LatestGAOrPatchVersion

    return [ArtifactInfo]::new($artifactId, $groupId, $latestGAOrPatchVersion)
}

# Parse the dependency information for each of the artifact from maven.
function UpdateDependencies($ArtifactInfos) {
    foreach ($artifactId in $ArtifactInfos.Keys) {
        $deps = @{}
        $sdkVersion = $ArtifactInfos[$artifactId].LatestGAOrPatchVersion
        $groupPath = $ArtifactInfos[$artifactId].GroupId -replace '\.', '/'
        # Use the internal Azure Artifacts feed instead of repo1.maven.org because the public
        # Maven Central endpoint is blocked on the build agent network.
        $pomFileUri = "$PackageRepositoryUri/$groupPath/$artifactId/$sdkVersion/$artifactId-$sdkVersion.pom"
        $headers = @{}
        if ($env:SYSTEM_ACCESSTOKEN -and $PackageRepositoryUri -match "pkgs.dev.azure.com") {
          $headers["Authorization"] = "Bearer $env:SYSTEM_ACCESSTOKEN"
        }
        $webResponseObj = Invoke-WebRequest -Uri $pomFileUri -UserAgent "azure-sdk-for-java" -Headers $headers
        $dependencies = ([xml]$webResponseObj.Content).project.dependencies.dependency | Where-Object { (([String]::IsNullOrWhiteSpace($_.scope)) -or ($_.scope -eq 'compile')) }
        $dependencies | ForEach-Object { $deps[$_.artifactId] = $_.version }
        $ArtifactInfos[$artifactId].Dependencies = $deps
    }

    return
}

# Update CII information for the artifacts.
function UpdateCIInformation($ArtifactInfos) {
    foreach ($artifactId in $ArtifactInfos.Keys) {
        $arInfo = [ArtifactInfo]$ArtifactInfos[$artifactId]
        $serviceDirectory = $arInfo.ServiceDirectoryName

        if (!$serviceDirectory) {
            $pkgProperties = [PackageProps](Get-PkgProperties -PackageName $artifactId -ServiceDirectory $serviceDirectory -GroupId $arInfo.GroupId)
            $arInfo.ServiceDirectoryName = $pkgProperties.ServiceDirectory
            $arInfo.ArtifactDirPath = $pkgProperties.DirectoryPath
            $arInfo.CurrentPomFileVersion = $pkgProperties.Version
            $arInfo.ChangeLogPath = $pkgProperties.ChangeLogPath
            $arInfo.ReadMePath = $pkgProperties.ReadMePath
        }

        $arInfo.PipelineName = GetPipelineName -ArtifactId $arInfo.ArtifactId -ArtifactDirPath $arInfo.ArtifactDirPath
    }
}

<#
.SYNOPSIS
Adds a missing Azure dependency's latest GA/patch version to the patch comparison map.

.DESCRIPTION
Returns $true when $LatestVersions[$DependencyId] is available after this function runs:
either it was already present, or it was resolved from Maven and added to $LatestVersions.

Returns $false when the dependency should not participate in patch-version comparison:
it is already known to be unresolved, is not an Azure artifact, has no usable GA/patch
version on Maven, or returns 404 from Maven metadata lookup. Other Maven/feed errors are
re-thrown because they can make patch analysis unreliable.
#>
function TryAddLatestDependencyVersionFromMaven($DependencyId, [hashtable]$LatestVersions, [hashtable]$UnresolvedDependencies) {
    # Dependency versions already in the patch graph take precedence. This includes
    # artifacts from patch_release_client.txt and artifacts already selected for a
    # patch in the current fixed-point pass.
    if ($LatestVersions.ContainsKey($DependencyId)) {
        return $true
    }

    # Cache missing dependencies so repeated references from multiple artifacts do
    # not repeatedly call the Maven feed during each fixed-point pass.
    if ($UnresolvedDependencies.ContainsKey($DependencyId)) {
        return $false
    }

    # Keep the original behavior for third-party packages: only Azure artifacts can
    # cause automated patch releases.
    if ($DependencyId -notlike "azure-*") {
        $UnresolvedDependencies[$DependencyId] = $true
        return $false
    }

    try {
        # Some common Azure dependencies (for example azure-core and azure-identity)
        # are not listed in patch_release_client.txt. Resolve their latest GA/patch
        # version from the Maven feed so dependents can still be detected.
        $dependencyInfo = GetVersionInfoForMavenArtifact -ArtifactId $DependencyId -GroupId "com.azure"
        if ($dependencyInfo -and -not [String]::IsNullOrWhiteSpace($dependencyInfo.LatestGAOrPatchVersion)) {
            $LatestVersions[$DependencyId] = $dependencyInfo.LatestGAOrPatchVersion
            return $true
        }

        $UnresolvedDependencies[$DependencyId] = $true
        return $false
    } catch {
        $statusCode = $null
        if ($_.Exception.Response -and $_.Exception.Response.StatusCode) {
            $statusCode = [int]$_.Exception.Response.StatusCode
        }

        # A 404 means the dependency is not published as a com.azure Maven artifact,
        # so it should be treated like an ignored external dependency.
        if ($statusCode -eq 404) {
            Write-Verbose "Could not find Maven metadata for dependency '$DependencyId' in group 'com.azure'."
            $UnresolvedDependencies[$DependencyId] = $true
            return $false
        }

        throw
    }
}

function Test-IsAzureResourceManagerUmbrella([ArtifactInfo]$ArtifactInfo) {
    return $ArtifactInfo.GroupId -eq "com.azure.resourcemanager" -and $ArtifactInfo.ArtifactId -eq "azure-resourcemanager"
}

function Test-IsAzureResourceManagerSubLibraryDependency([string]$DependencyId) {
    return $DependencyId -like "azure-resourcemanager-*"
}

function Test-IsPatchCompatibleDependencyUpdate([string]$DeclaredVersion, [string]$LatestVersion) {
    $declaredSemver = [AzureEngSemanticVersion]::ParseVersionString($DeclaredVersion)
    $latestSemver = [AzureEngSemanticVersion]::ParseVersionString($LatestVersion)

    if (!$declaredSemver -or !$latestSemver -or $declaredSemver.IsPrerelease -or $latestSemver.IsPrerelease) {
        return $false
    }

    return $declaredSemver.Major -eq $latestSemver.Major -and $declaredSemver.Minor -eq $latestSemver.Minor
}

function Test-IsIgnorableResourceManagerPrereleaseUpdate([string]$DeclaredVersion, [string]$LatestVersion) {
    $declaredSemver = [AzureEngSemanticVersion]::ParseVersionString($DeclaredVersion)
    $latestSemver = [AzureEngSemanticVersion]::ParseVersionString($LatestVersion)

    if (!$declaredSemver -or !$latestSemver -or !$latestSemver.IsPrerelease) {
        return $false
    }

    # A beta for the next minor version has not had its corresponding GA yet, so it should not block an umbrella patch.
    # E.g. we accept 2.55.x -> 2.56.0-beta.x, but not 2.55.x -> 2.57.0-beta.x, since there's 2.56.0 before 2.57.0-beta.x, and no before 2.56.0-beta.x
    return $latestSemver.Major -eq $declaredSemver.Major -and $latestSemver.Minor -le ($declaredSemver.Minor + 1)
}

# Find all the artifacts that will need to be patched based on dependency analysis.
# Iterates until no more patches are found (fixed-point), so the result is correct
# regardless of artifact ordering in patch_release_client.txt.
# Dependencies in the patch list are checked directly; missing Azure dependencies
# are resolved from Maven. External dependencies (reactor-core, jackson, etc.) are ignored.
function FindArtifactsThatNeedPatching($ArtifactInfos) {
    $latestVersions = @{}
    $unresolvedDependencies = @{}
    # Avoid repeated warnings for the same non-patch dependency update across fixed-point passes.
    $reportedNonPatchResourceManagerDependencies = @{}
    foreach ($arId in $ArtifactInfos.Keys) {
        $latestVersions[$arId] = $ArtifactInfos[$arId].LatestGAOrPatchVersion
    }

    do {
        $changed = $false
        foreach ($arId in $ArtifactInfos.Keys) {
            $arInfo = $ArtifactInfos[$arId]
            if ($arInfo.FutureReleasePatchVersion) { continue }

            if (Test-IsAzureResourceManagerUmbrella -ArtifactInfo $arInfo) {
                # Scan all azure-resourcemanager-* sub-dependencies before deciding whether the umbrella can be patched.
                $hasPatchCompatibleResourceManagerDependencyUpdate = $false
                $hasNonPatchResourceManagerDependencyUpdate = $false

                foreach ($depId in $arInfo.Dependencies.Keys) {
                    if (-not (Test-IsAzureResourceManagerSubLibraryDependency -DependencyId $depId)) {
                        continue
                    }

                    # Use the already-known latest version when the sub-dependency is in the patch list; otherwise resolve it from Maven.
                    # If Maven cannot resolve it, skip this dependency because there is no reliable version to compare against.
                    if (-not $latestVersions.ContainsKey($depId) -and
                        -not (TryAddLatestDependencyVersionFromMaven -DependencyId $depId -LatestVersions $latestVersions -UnresolvedDependencies $unresolvedDependencies)) {
                        continue
                    }

                    $declaredVersion = $arInfo.Dependencies[$depId]
                    $latestVersion = $latestVersions[$depId]
                    if ($declaredVersion -eq $latestVersion) {
                        continue
                    }

                    # Track any patch-compatible sub-dependency update, but keep scanning in case another sub-dependency requires a minor umbrella release.
                    if (Test-IsPatchCompatibleDependencyUpdate -DeclaredVersion $declaredVersion -LatestVersion $latestVersion) {
                        $hasPatchCompatibleResourceManagerDependencyUpdate = $true
                        continue
                    }

                    if (Test-IsIgnorableResourceManagerPrereleaseUpdate -DeclaredVersion $declaredVersion -LatestVersion $latestVersion) {
                        continue
                    }

                    $hasNonPatchResourceManagerDependencyUpdate = $true
                    $warningKey = "${depId}:${declaredVersion}:${latestVersion}"
                    if (-not $reportedNonPatchResourceManagerDependencies.ContainsKey($warningKey)) {
                        Write-Warning "Skipping patch release for azure-resourcemanager: sub-dependency $depId moved from $declaredVersion to $latestVersion (non-patch bump). Schedule a manual minor release of azure-resourcemanager instead."
                        $reportedNonPatchResourceManagerDependencies[$warningKey] = $true
                    }
                }

                if ($hasNonPatchResourceManagerDependencyUpdate) {
                    continue
                }

                if ($hasPatchCompatibleResourceManagerDependencyUpdate) {
                    $patchVersion = GetPatchVersion -ReleaseVersion $arInfo.LatestGAOrPatchVersion
                    $arInfo.FutureReleasePatchVersion = $patchVersion
                    $latestVersions[$arId] = $patchVersion
                    $changed = $true
                    continue
                }
            }

            foreach ($depId in $arInfo.Dependencies.Keys) {
                # The umbrella-specific scan above already handled azure-resourcemanager-* dependencies.
                # Do not let the generic fallback treat ignored prerelease updates as patch triggers.
                if ((Test-IsAzureResourceManagerUmbrella -ArtifactInfo $arInfo) -and
                    (Test-IsAzureResourceManagerSubLibraryDependency -DependencyId $depId)) {
                    continue
                }

                if (-not $latestVersions.ContainsKey($depId) -and
                    -not (TryAddLatestDependencyVersionFromMaven -DependencyId $depId -LatestVersions $latestVersions -UnresolvedDependencies $unresolvedDependencies)) {
                    continue
                }

                if ($arInfo.Dependencies[$depId] -ne $latestVersions[$depId]) {
                    $patchVersion = GetPatchVersion -ReleaseVersion $arInfo.LatestGAOrPatchVersion
                    $arInfo.FutureReleasePatchVersion = $patchVersion
                    $latestVersions[$arId] = $patchVersion
                    $changed = $true
                    break
                }
            }
        }
    } while ($changed)
}

# Update dependencies in the version client file.
function UpdateDependenciesInVersionClient([hashtable]$ArtifactInfos) {
    ## We need to update the version_client.txt to have the correct versions in place.
    foreach ($artifactId in $ArtifactInfos.Keys) {
        $newDependencyVersion = $ArtifactInfos[$artifactId].FutureReleasePatchVersion

        if (!$newDependencyVersion) {
            $newDependencyVersion = $ArtifactInfos[$artifactId].LatestGAOrPatchVersion
        }

        $currentFileVersion = $ArtifactInfos[$artifactId].CurrentPomFileVersion

        if ($newDependencyVersion) {
            $cmdOutput = SetDependencyVersion -GroupId $ArtifactInfos[$artifactId].GroupId -ArtifactId $artifactId -Version $newDependencyVersion
            $cmdOutput = SetCurrentVersion -GroupId $ArtifactInfos[$artifactId].GroupId -ArtifactId $artifactId -Version $currentFileVersion
        }
    }
}

# Get the release version for the next bom artifact.
function GetNextBomVersion() {
    $pkgProperties = [PackageProps](Get-PkgProperties -PackageName "azure-sdk-bom" -GroupId "com.azure")
    $currentVersion = $pkgProperties.Version

    $patchVersion = GetPatchVersion -ReleaseVersion $currentVersion
    return $patchVersion
}

# Find the correct order in which all the artifacts need to be released.
function TopologicalSortUtil($ArtifactId, $ArtifactInfos, $ArtifactIds, $Visited, $Order) {
    $Visited[$ArtifactId] = $true

    # Find all dependencies that are also getting patched.
    $adjDependencies = $ArtifactInfos[$ArtifactId].Dependencies.Keys | Where-Object { $ArtifactIds -Contains $_ }

    foreach ($arId in $adjDependencies) {
        if (!$Visited.ContainsKey($arId)) {
            TopologicalSortUtil -ArtifactId $arId -ArtifactInfos $ArtifactInfos -ArtifactIds $ArtifactIds -Visited $Visited -Order $Order
        }
    }

    $cmdOutput = $Order.Add($ArtifactId)
}

function GetTopologicalSort($ArtifactIds, $ArtifactInfos) {
    $order = [System.Collections.ArrayList]::new()
    # $reverseOrder = @()
    $visited = [System.Collections.Hashtable]::new()
    foreach ($artifactId in $ArtifactIds) {
        if (!$visited.ContainsKey($artifactId)) {
            TopologicalSortUtil -ArtifactId $artifactId -ArtifactInfos $ArtifactInfos -ArtifactIds $ArtifactIds -Visited $visited -Order $order
        }
    }

    $pipelineOrdered = @()
    $visited = @{}

    for($i=0; $i -lt $order.Count; $i++) {
        $arId = $order[$i]
        if($null -ne $visited[$arId]) {
            continue;
        }

        $visited[$arId] = $true
        $pipelineName = $ArtifactInfos[$arId].PipelineName
        $pipelineOrdered += @{
            ArtifactId = $arId
            PipelineName = $pipelineName
        }

        for($j=$i; $j -lt $order.Count; $j++) {
            $curArId = $order[$j]
            if($null -eq $visited[$curArId] -and $pipelineName -eq $ArtifactInfos[$curArId].PipelineName) {
                $pipelineOrdered += @{
                    ArtifactId = $curArId
                    PipelineName = $pipelineName
                }
                $visited[$curArId] = $true
            }
        }
    }

    return $pipelineOrdered
}

# Create the dependency section for the BOM artifact.
function CreateDependencyXmlElement($Artifact, [xml]$Doc) {
    $xmlns = $Doc.Project.xmlns
    $xsi = $Doc.Project.xsi

    $dependency = $Doc.CreateElement("dependency", $xmlns);
    $groupId = $Doc.CreateElement("groupId", $xmlns);
    $groupId.InnerText = $Artifact.GroupId
    $cmdOutput = $dependency.AppendChild($groupId);
    $artifactId = $Doc.CreateElement("artifactId", $xmlns);
    $artifactId.InnerText = $Artifact.ArtifactId
    $cmdOutput = $dependency.AppendChild($artifactId);
    $version = $Doc.CreateElement("version", $xmlns);
    $version.InnerText = $Artifact.Version
    $cmdOutput = $dependency.AppendChild($version);

    $dependencies = $bomFileContent.GetElementsByTagName("dependencies")[0]
    $cmdOutput = $dependencies.AppendChild($dependency)
}

# Generate BOM file for the given artifacts.
function GenerateBOMFile($ArtifactInfos, $BomFileBranchName, [bool]$UseCurrentBranch = $false) {
    $gaArtifacts = @()

    foreach ($artifact in $ArtifactInfos.Values) {
        $version = $artifact.LatestGAOrPatchVersion

        if ($null -eq $version) {
            $version = $artifact.FutureReleasePatchVersion
        }

        $gaArtifacts += @{
            GroupId    = $artifact.GroupId
            ArtifactId = $artifact.ArtifactId
            Version    = $version
        }
    }

    $gaArtifacts = $gaArtifacts | Sort-Object -Property ArtifactId

    #Now we need to create the BOM file.
    $bomFileContent = [xml](Get-Content -Path $BomFilePath)
    $dependencyManagement = $bomFileContent.project.dependencyManagement
    $dependencies = $dependencyManagement.dependencies
    $cmdOutput = $dependencyManagement.RemoveChild($dependencies)
    $dependencies = $bomFileContent.CreateElement("dependencies", $bomFileContent.Project.xmlns);
    $cmdOutput = $dependencyManagement.AppendChild($dependencies);

    foreach ($dependency in $gaArtifacts) {
        CreateDependencyXmlElement -Artifact $dependency -Doc $bomFileContent
    }

    $currentBranchName = GetCurrentBranchName
    try {
        UpdateDependenciesInVersionClient -ArtifactInfos $ArtifactInfos
        $releaseVersion = $bomFileContent.project.version
        $patchVersion = GetPatchVersion -ReleaseVersion $releaseVersion
        $remoteName = GetRemoteName
        $base = if ($UseCurrentBranch) { "HEAD" } else { "$remoteName/main" }
        Write-Host "git checkout -b $BomFileBranchName $base"
        $cmdOutput = git checkout -b $BomFileBranchName $base
        $bomFileContent.Save($BomFilePath)
        Write-Host "git add $BomFilePath"
        git add $BomFilePath
        $content = GetChangeLogContentFromMessage -ContentMessage '- Updated Azure SDK dependency versions to the latest releases.'
        UpdateChangeLogEntry -ChangeLogPath $BomChangeLogPath -PatchVersion $patchVersion -ArtifactId "azure-sdk-bom" -Content $content
        GitCommit -Message "Prepare BOM for release version $releaseVersion"
        Write-Host "git push -c user.name=`"azure-sdk`" -c user.email=`"azuresdk@microsoft.com`" -f $remoteName $BomFileBranchName"
        git push -c user.name="azure-sdk" -c user.email="azuresdk@microsoft.com" -f $remoteName $BomFileBranchName
    }
    finally {
        Write-Host "git checkout $currentBranchName"
        git checkout $currentBranchName
    }
}

# Generate json report for all the artifacts that need to be patched.
function GenerateJsonReport($ArtifactPatchInfos, $PatchBranchName, $BomFileBranchName) {
    $patchReport = @{
        PathBranchName = $PatchBranchName
        ArtifactsToPatch = $ArtifactPatchInfos
    }

    $jsonReport = @{
        BomBranchName = $BomFileBranchName
        PatchReport = $patchReport
    }

    $jsonReport | ConvertTo-Json -Depth 5 | Out-File $PatchReportFile
}

# This is an HTML report for all the artifacts that are being patched.
function GenerateHtmlReport($Artifacts, $PatchBranchName, $BomFileBranchName) {
    $count = $Artifacts.Count
    $index = 0
    $html = @()
    $html += "<head><title>Patch Report</title></head><body><table border='1'><tr><th>Artifact</th><th>PipelineName</th><th>Release Branch</th><tr>"
    $pipelineCountIndex = 0
    foreach ($artifact in $Artifacts) {
        $artifactId = $artifact.ArtifactId
        $pipelineName = $artifact.PipelineName
        $pipelineNameCount = $Artifacts | Where-Object $_.PipelineName -eq $pipelineName

        $html += "<tr>"
        if ($index++ -eq 0) {
            $html += "<td  rowspan='$count'>$PatchBranchName</td>"
        }
        $html += "<td rowspan='$pipelineNameCount'>$pipelineName</td>"
        $html += "<td>$artifactId</td>"
        $html += "</tr>"
    }

    $html += "<tr><td>$BomFileBranchName</td><td>azure-sdk-bom</td></tr>"
    $html += "</table>"
    $currentDate = Get-Date -Format "dddd MM/dd/yyyy HH:mm K"

    $html += "<p>Report generated on $currentDate </p>"
    $html | Out-File -FilePath $PatchReportFile -Force
}
