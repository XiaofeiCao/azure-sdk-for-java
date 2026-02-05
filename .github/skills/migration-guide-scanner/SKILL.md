---
name: migration-guide-scanner
description: |
    Scan Azure SDK for Java repository and compile comprehensive Track 1 (legacy) to Track 2 (current) package mappings. Use this skill when:
    - Finding migration guides for Track 1 to Track 2 library migrations
    - Collecting all Track 1 → Track 2 package mappings (with or without migration guides)
    - Generating inventory of available migration documentation
    - User asks about migrating from com.microsoft.azure.* packages to com.azure.* packages
    - Identifying which Track 2 package replaces a specific Track 1 package
---

# Migration Guide Scanner

Scan the Azure SDK for Java repository and compile comprehensive Track 1 (legacy) to Track 2 (current) package mappings, including both documented migrations and known package equivalents.

## Terminology

- **Track 1**: Legacy packages with group ID `com.microsoft.azure` or artifact prefix `microsoft-azure-*`
- **Track 2**: Current packages with group ID `com.azure` or `com.azure.resourcemanager`

## Scanning Procedure

### Step 1: Search for Migration Guide Files

Search for migration guide files using ALL of these patterns (note variations in naming):

```
**/MIGRATION*.md          # MIGRATION.md, MIGRATION_GUIDE.md
**/migration*.md          # migration.md, migration-guide.md, migration_guide.md
**/*migrat*.md            # Any file containing "migrat" in the name
**/migrationGuide*/**/*.md # Files in migrationGuides folders
**/docs/migration*.md     # Migration docs in docs subfolders
```

For each file found, extract:

- Track 2 package path (parent directory)
- Track 1 package name (from file content - look for `com.microsoft.azure.*` references)

### Step 2: Search CHANGELOGs for Migration Guide Sections

Search for "Migration Guide" in CHANGELOG.md files:

```
grep "Migration Guide" in **/CHANGELOG.md
```

For matches, read the surrounding context to extract:

- Track 2 package name (from CHANGELOG location)
- Track 1 package reference (from migration section content)

### Step 3: Search READMEs for Migration References

Search for migration links in README files:

```
grep "migration" in **/README.md (case-insensitive)
```

Look for patterns like:

- "migration guide" links
- References to older/legacy packages
- Deprecation notices pointing to new packages

### Step 4: Identify Track 1 Package Names

For each Track 2 package with a migration guide, identify the Track 1 equivalent by:

1. Checking explicit references in migration guide content
2. Common naming patterns:
   | Track 2 Pattern | Track 1 Pattern |
   |-----------------|-----------------|
   | `azure-messaging-*` | `microsoft-azure-*` or `azure-*` |
   | `azure-storage-*` | `azure-storage` (v8.x) |
   | `azure-resourcemanager-*` | `azure-mgmt-*` or `azure` |
   | `azure-security-keyvault-*` | `azure-keyvault` |

3. Look for `com.microsoft.azure` group ID references

### Step 5: Validate Package Deprecation Status

Before including a `com.microsoft.azure` package in the migration list, verify it is actually deprecated by running the validation script:

**Run the Python script to check Maven Central:**

```bash
python .github/skills/migration-guide-scanner/scripts/check_maven_packages.py [package-names...]
```

The script will:

1. Query Maven Central with proper headers:
    - `Content-Signal: search=yes,ai-train=no`
    - `User-Agent: azure-sdk-for-java`
2. Check `<lastUpdated>` timestamp from `maven-metadata.xml`
3. Categorize packages as "active" (updated within 2 years) or "deprecated"

**Packages to EXCLUDE (actively maintained, not deprecated)**:

- `com.microsoft.azure:msal4j` - Microsoft Authentication Library
- `com.microsoft.azure:msal4j-persistence-extension` - MSAL extension
- `com.microsoft.azure:msal4j-brokers` - MSAL brokers
- `com.microsoft.azure:qpid-proton-j-extensions` - AMQP extensions
- `com.microsoft.azure:azure-batch` - Batch compute
- `com.microsoft.azure:applicationinsights-core` - Application Insights
- Any package with releases in the last 2 years

**Manual check**: Visit `https://repo1.maven.org/maven2/com/microsoft/azure/{artifact-id}/maven-metadata.xml`

### Step 6: Include Known Track 1 → Track 2 Mappings

In addition to migration guides found in the repository, include these known package mappings. These are Track 1 packages that have Track 2 equivalents but may not have dedicated migration guide files.

**Only include packages that have NOT been updated in the last 2 years on Maven Central.**

#### Data Plane Libraries

| Track 1 Package                                 | Track 2 Package                                                                                               | Notes                            |
| ----------------------------------------------- | ------------------------------------------------------------------------------------------------------------- | -------------------------------- |
| com.microsoft.azure:azure-servicebus            | com.azure:azure-messaging-servicebus                                                                          | Has migration guide              |
| com.microsoft.azure:azure-eventhubs             | com.azure:azure-messaging-eventhubs                                                                           | Has migration guide              |
| com.microsoft.azure:azure-eventhubs-eph         | com.azure:azure-messaging-eventhubs-checkpointstore-blob                                                      | Event Processor Host             |
| com.microsoft.azure:azure-eventgrid             | com.azure:azure-messaging-eventgrid                                                                           | Has migration guide              |
| com.microsoft.azure:azure-storage               | com.azure:azure-storage-blob, azure-storage-queue, azure-storage-file-share                                   | Split into multiple packages     |
| com.microsoft.azure:azure-keyvault              | com.azure:azure-security-keyvault-secrets, azure-security-keyvault-keys, azure-security-keyvault-certificates | Split into multiple packages     |
| com.microsoft.azure:azure-batch                 | com.azure:azure-compute-batch                                                                                 |                                  |
| com.microsoft.azure:azure-cosmosdb              | com.azure:azure-cosmos                                                                                        |                                  |
| com.microsoft.azure:applicationinsights-core    | com.azure:azure-monitor-opentelemetry-autoconfigure                                                           |                                  |
| com.microsoft.azure:azure-data-appconfiguration | com.azure:azure-data-appconfiguration                                                                         | Same artifact, different group   |
| com.microsoft.azure:azure-identity              | com.azure:azure-identity                                                                                      | Same artifact, different group   |
| com.microsoft.azure.cognitiveservices:\*        | com.azure:azure-ai-\*                                                                                         | Cognitive Services → AI packages |

#### Management Libraries

| Track 1 Package                                  | Track 2 Package                                                   | Notes                    |
| ------------------------------------------------ | ----------------------------------------------------------------- | ------------------------ |
| com.microsoft.azure:azure                        | com.azure.resourcemanager:azure-resourcemanager                   | Aggregate management SDK |
| com.microsoft.azure:azure-mgmt-resources         | com.azure.resourcemanager:azure-resourcemanager-resources         |                          |
| com.microsoft.azure:azure-mgmt-compute           | com.azure.resourcemanager:azure-resourcemanager-compute           |                          |
| com.microsoft.azure:azure-mgmt-storage           | com.azure.resourcemanager:azure-resourcemanager-storage           |                          |
| com.microsoft.azure:azure-mgmt-network           | com.azure.resourcemanager:azure-resourcemanager-network           |                          |
| com.microsoft.azure:azure-mgmt-keyvault          | com.azure.resourcemanager:azure-resourcemanager-keyvault          |                          |
| com.microsoft.azure:azure-mgmt-sql               | com.azure.resourcemanager:azure-resourcemanager-sql               |                          |
| com.microsoft.azure:azure-mgmt-cosmosdb          | com.azure.resourcemanager:azure-resourcemanager-cosmos            |                          |
| com.microsoft.azure:azure-mgmt-eventhub          | com.azure.resourcemanager:azure-resourcemanager-eventhubs         |                          |
| com.microsoft.azure:azure-mgmt-servicebus        | com.azure.resourcemanager:azure-resourcemanager-servicebus        |                          |
| com.microsoft.azure:azure-mgmt-appservice        | com.azure.resourcemanager:azure-resourcemanager-appservice        |                          |
| com.microsoft.azure:azure-mgmt-containerregistry | com.azure.resourcemanager:azure-resourcemanager-containerregistry |                          |
| com.microsoft.azure:azure-mgmt-containerservice  | com.azure.resourcemanager:azure-resourcemanager-containerservice  |                          |
| com.microsoft.azure:azure-mgmt-dns               | com.azure.resourcemanager:azure-resourcemanager-dns               |                          |
| com.microsoft.azure:azure-mgmt-monitor           | com.azure.resourcemanager:azure-resourcemanager-monitor           |                          |
| com.microsoft.azure:azure-mgmt-redis             | com.azure.resourcemanager:azure-resourcemanager-redis             |                          |
| com.microsoft.azure:azure-mgmt-trafficmanager    | com.azure.resourcemanager:azure-resourcemanager-trafficmanager    |                          |
| com.microsoft.azure:azure-mgmt-cdn               | com.azure.resourcemanager:azure-resourcemanager-cdn               |                          |
| com.microsoft.azure:azure-mgmt-\*                | com.azure.resourcemanager:azure-resourcemanager-\*                | General pattern          |

### Step 6: Generate Output

Produce a Markdown table with all mappings, indicating whether a migration guide exists. Use GitHub raw content URLs for migration guides (not relative paths):

```markdown
# Azure SDK for Java Migration Guide Index

## Track 1 to Track 2 Package Mappings

| Track 1 Package                      | Track 2 Package                      | Migration Guide                                                                                                                      |
| ------------------------------------ | ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------ |
| com.microsoft.azure:azure-servicebus | com.azure:azure-messaging-servicebus | [Link](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/servicebus/azure-messaging-servicebus/migration-guide.md) |
| com.microsoft.azure:azure-batch      | com.azure:azure-compute-batch        | No guide available                                                                                                                   |
| ...                                  | ...                                  | ...                                                                                                                                  |
```

## Search Patterns Reference

### File patterns to search:

- `**/MIGRATION*.md` - uppercase patterns (MIGRATION.md, MIGRATION_GUIDE.md)
- `**/migration*.md` - lowercase patterns (migration-guide.md, migration_guide.md)
- `**/*migrat*.md` - any file containing "migrat" in name
- `**/migrationGuide*/*.md` - files in migrationGuides folders
- `**/docs/migration*.md` - migration docs in docs subfolders
- `**/CHANGELOG.md` (for "Migration Guide" sections)
- `**/README.md` (for migration references)

### Content patterns to match:

- `com.microsoft.azure` - Track 1 group ID
- `com.microsoft.azure.management` - Track 1 management group ID
- `microsoft-azure-` - Track 1 artifact prefix
- `azure-mgmt-` - Track 1 management artifact prefix
- `Migration Guide` - Section header in CHANGELOGs
- `migrating from` - Migration context phrases

## Example Output

```markdown
# Azure SDK for Java Migration Guide Index

## Track 1 to Track 2 Package Mappings

| Track 1 Package (Legacy)                                   | Track 2 Package (Current)                      | Migration Guide                                                                                                                                              |
| ---------------------------------------------------------- | ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| com.microsoft.azure:azure-servicebus                       | com.azure:azure-messaging-servicebus           | [Migration Guide](https://aka.ms/azsdk/java/migrate/sb)                                                                                                      |
| com.microsoft.azure:azure-eventhubs                        | com.azure:azure-messaging-eventhubs            | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/eventhubs/azure-messaging-eventhubs/migration-guide.md)                |
| com.microsoft.azure:azure-eventgrid                        | com.azure:azure-messaging-eventgrid            | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/eventgrid/azure-messaging-eventgrid/migration-guide.md)                |
| com.microsoft.azure:azure-storage (v8)                     | com.azure:azure-storage-blob                   | [V8 to V12](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-blob/migrationGuides/V8_V12.md)                        |
| com.microsoft.azure:azure-storage (v10/11)                 | com.azure:azure-storage-blob                   | [V10 to V12](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-blob/migrationGuides/V10_V12.md)                      |
| com.microsoft.azure:azure-keyvault                         | com.azure:azure-security-keyvault-secrets      | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-secrets/migration_guide.md)           |
| com.microsoft.azure:azure-keyvault                         | com.azure:azure-security-keyvault-keys         | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-keys/migration_guide.md)              |
| com.microsoft.azure:azure-keyvault                         | com.azure:azure-security-keyvault-certificates | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-certificates/migration_guide.md)      |
| com.microsoft.azure.management:\*                          | com.azure.resourcemanager:\*                   | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/resourcemanager/docs/MIGRATION_GUIDE.md)                               |
| com.microsoft.azure.cognitiveservices:azure-formrecognizer | com.azure:azure-ai-documentintelligence        | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/documentintelligence/azure-ai-documentintelligence/MIGRATION_GUIDE.md) |
| Cosmos DB Spark Connector (Spark 2.4)                      | azure-cosmos-spark_3                           | [Migration Guide](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/cosmos/azure-cosmos-spark_3/docs/migration.md)                         |
```

## Notes

- Some packages may have multiple migration guides (e.g., V8→V12 and V10→V12)
- External migration guides (aka.ms links) should be preserved and preferred when available
- Use GitHub raw content URLs: `https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/...`
- Resource Manager packages follow a different pattern with `azure-resourcemanager-*` prefix
- **Exclude** azure-core to azure-core-v2 migration (sdk/tools/azure-openrewrite/MIGRATION.md) - not ready for production
- **Exclude** Spring Cloud Azure packages (`com.azure.spring`) - handled separately

## Step 7: Update Migration Guide Index

After scanning, update the index file at:

```
.github/skills/migration-guide-scanner/assets/MIGRATION_GUIDE_INDEX.md
```

The index file should include:

1. **Maven Central Scan Summary** - Active/Deprecated/Not found counts
2. **Data Plane Libraries (With Migration Guides)** - Organized by service category
3. **Management Libraries (With Migration Guide)** - Resource manager packages
4. **Known Track 1 → Track 2 Mappings (Without Guides)** - Package equivalents without dedicated documentation
5. **Summary Table** - Counts by category
6. **Actively Maintained Packages** - List of `com.microsoft.azure` packages that should NOT be migrated
7. **File Locations Reference** - All migration guide files with GitHub raw URLs

Update the "Generated on" date at the top of the file to reflect when the scan was performed.
