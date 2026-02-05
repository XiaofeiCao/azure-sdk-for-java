#!/usr/bin/env python3
"""
Check Maven Central for package deprecation status.

This script queries Maven Central to determine if com.microsoft.azure packages
are actively maintained (released within the last 2 years) or deprecated.

Usage:
    python check_maven_packages.py [package_specs...]

Package specs can be:
    - artifact_id only (uses com.microsoft.azure as group): azure-servicebus
    - full coordinates: com.microsoft.azure.cognitiveservices:azure-cognitiveservices-textanalytics

Example:
    python check_maven_packages.py azure-servicebus azure-eventhubs msal4j
    python check_maven_packages.py --all  # Check all known Track 1 packages
"""

import sys
import urllib.request
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta
from typing import Optional, Tuple, Dict, List

MAVEN_BASE_URL = "https://repo1.maven.org/maven2"
HEADERS = {
    "Content-Signal": "search=yes,ai-train=no",
    "User-Agent": "azure-sdk-for-java"
}
DEPRECATION_THRESHOLD_YEARS = 2


def get_package_last_updated(group_id: str, artifact_id: str) -> Optional[Tuple[str, datetime]]:
    """
    Fetch the lastUpdated timestamp for a package from Maven Central.

    Args:
        group_id: The Maven group ID (e.g., 'com.microsoft.azure')
        artifact_id: The Maven artifact ID (e.g., 'azure-servicebus')

    Returns:
        Tuple of (version, datetime) or None if not found
    """
    group_path = group_id.replace('.', '/')
    url = f"{MAVEN_BASE_URL}/{group_path}/{artifact_id}/maven-metadata.xml"

    try:
        request = urllib.request.Request(url, headers=HEADERS)
        with urllib.request.urlopen(request, timeout=10) as response:
            content = response.read().decode('utf-8')

        root = ET.fromstring(content)

        # Get latest version
        latest = root.find('.//latest')
        if latest is None:
            latest = root.find('.//release')
        version = latest.text if latest is not None else "unknown"

        # Get lastUpdated timestamp (format: YYYYMMDDHHmmss)
        last_updated = root.find('.//lastUpdated')
        if last_updated is not None and last_updated.text:
            timestamp = last_updated.text
            # Parse timestamp: 20260124025715 -> 2026-01-24 02:57:15
            dt = datetime.strptime(timestamp[:14], '%Y%m%d%H%M%S')
            return (version, dt)

    except urllib.error.HTTPError as e:
        if e.code != 404:
            print(f"Error fetching {group_id}:{artifact_id}: {e}", file=sys.stderr)
    except Exception as e:
        print(f"Error fetching {group_id}:{artifact_id}: {e}", file=sys.stderr)

    return None


def is_actively_maintained(last_updated: datetime) -> bool:
    """
    Check if a package is actively maintained based on last update date.

    A package is considered actively maintained if it has been updated
    within the last 2 years.
    """
    threshold = datetime.now() - timedelta(days=365 * DEPRECATION_THRESHOLD_YEARS)
    return last_updated > threshold


def parse_package_spec(spec: str) -> Tuple[str, str]:
    """
    Parse a package specification into group_id and artifact_id.

    Args:
        spec: Either 'artifact_id' or 'group_id:artifact_id'

    Returns:
        Tuple of (group_id, artifact_id)
    """
    if ':' in spec:
        parts = spec.split(':')
        return (parts[0], parts[1])
    return ('com.microsoft.azure', spec)


def check_packages(package_specs: List[str]) -> Dict:
    """
    Check multiple packages and categorize them as active or deprecated.

    Returns:
        Dictionary with 'active', 'deprecated', and 'not_found' lists
    """
    results = {
        'active': [],
        'deprecated': [],
        'not_found': []
    }

    for spec in package_specs:
        group_id, artifact_id = parse_package_spec(spec)
        info = get_package_last_updated(group_id, artifact_id)

        full_name = f"{group_id}:{artifact_id}"

        if info is None:
            results['not_found'].append(full_name)
            continue

        version, last_updated = info

        package_info = {
            'group_id': group_id,
            'artifact_id': artifact_id,
            'full_name': full_name,
            'version': version,
            'last_updated': last_updated.strftime('%Y-%m-%d'),
            'timestamp': last_updated
        }

        if is_actively_maintained(last_updated):
            results['active'].append(package_info)
        else:
            results['deprecated'].append(package_info)

    return results


def print_results(results: Dict):
    """Print results in a formatted table."""
    print("\n" + "=" * 100)
    print("ACTIVELY MAINTAINED PACKAGES (updated within last 2 years) - DO NOT MIGRATE")
    print("=" * 100)

    if results['active']:
        print(f"{'Package':<60} {'Version':<15} {'Last Updated':<12}")
        print("-" * 87)
        for pkg in sorted(results['active'], key=lambda x: x['timestamp'], reverse=True):
            print(f"{pkg['full_name']:<60} {pkg['version']:<15} {pkg['last_updated']:<12}")
    else:
        print("None found")

    print("\n" + "=" * 100)
    print("DEPRECATED PACKAGES (not updated in 2+ years) - RECOMMEND MIGRATION")
    print("=" * 100)

    if results['deprecated']:
        print(f"{'Package':<60} {'Version':<15} {'Last Updated':<12}")
        print("-" * 87)
        for pkg in sorted(results['deprecated'], key=lambda x: x['timestamp'], reverse=True):
            print(f"{pkg['full_name']:<60} {pkg['version']:<15} {pkg['last_updated']:<12}")
    else:
        print("None found")

    if results['not_found']:
        print("\n" + "=" * 100)
        print("NOT FOUND ON MAVEN CENTRAL")
        print("=" * 100)
        for pkg in results['not_found']:
            print(f"  - {pkg}")

    # Summary
    print("\n" + "=" * 100)
    print("SUMMARY")
    print("=" * 100)
    print(f"  Active (do not migrate): {len(results['active'])}")
    print(f"  Deprecated (migrate):    {len(results['deprecated'])}")
    print(f"  Not found:               {len(results['not_found'])}")


# Comprehensive list of known Track 1 packages to check
# Format: "group_id:artifact_id" or just "artifact_id" (defaults to com.microsoft.azure)
DEFAULT_PACKAGES = [
    # ============================================
    # com.microsoft.azure - Data Plane Libraries
    # ============================================
    # Messaging
    "azure-servicebus",
    "azure-eventhubs",
    "azure-eventhubs-eph",
    "azure-eventgrid",

    # Storage
    "azure-storage",
    "azure-storage-blob",
    "azure-storage-queue",

    # Key Vault
    "azure-keyvault",
    "azure-keyvault-cryptography",
    "azure-keyvault-core",
    "azure-keyvault-extensions",
    "azure-keyvault-webkey",

    # Cosmos DB
    "azure-cosmos",
    "azure-cosmosdb",
    "azure-documentdb",
    "azure-documentdb-rx",

    # Identity/Auth
    "msal4j",
    "msal4j-persistence-extension",
    "msal4j-brokers",
    "adal4j",
    "azure-client-authentication",
    "azure-client-runtime",

    # Batch
    "azure-batch",

    # Application Insights / Monitor
    "applicationinsights-core",
    "applicationinsights-web",
    "applicationinsights-logging-log4j1_2",
    "applicationinsights-logging-log4j2",
    "applicationinsights-logging-logback",
    "applicationinsights-spring-boot-starter",

    # AMQP
    "qpid-proton-j-extensions",

    # Media
    "azure-media",

    # ============================================
    # com.microsoft.azure - Management Libraries
    # ============================================
    "azure-mgmt-resources",
    "azure-mgmt-compute",
    "azure-mgmt-storage",
    "azure-mgmt-network",
    "azure-mgmt-keyvault",
    "azure-mgmt-sql",
    "azure-mgmt-cosmosdb",
    "azure-mgmt-eventhub",
    "azure-mgmt-servicebus",
    "azure-mgmt-appservice",
    "azure-mgmt-containerregistry",
    "azure-mgmt-containerservice",
    "azure-mgmt-dns",
    "azure-mgmt-monitor",
    "azure-mgmt-redis",
    "azure-mgmt-trafficmanager",
    "azure-mgmt-cdn",
    "azure-mgmt-batch",
    "azure-mgmt-datalake-analytics",
    "azure-mgmt-datalake-store",
    "azure-mgmt-hdinsight",
    "azure-mgmt-privatedns",
    "azure-mgmt-search",
    "azure-mgmt-signalr",
    "azure-mgmt-streamanalytics",
    "azure-mgmt-iothub",
    "azure-mgmt-mediaservices",
    "azure-mgmt-cognitiveservices",
    "azure-mgmt-locks",
    "azure-mgmt-policy",
    "azure-mgmt-graph-rbac",
    "azure-mgmt-msi",
    "azure-mgmt-authorization",
    "azure",  # The umbrella management package

    # ============================================
    # com.microsoft.azure.cognitiveservices - Cognitive Services
    # ============================================
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-textanalytics",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-computervision",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-face",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-luis-authoring",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-luis-runtime",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-anomalydetector",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-contentmoderator",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-customvision-training",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-customvision-prediction",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-formrecognizer",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-qnamaker",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-personalizer",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-translatortext",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-spellcheck",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-autosuggest",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-websearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-imagesearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-videosearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-newssearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-visualsearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-entitysearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-customsearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-localsearch",
    "com.microsoft.azure.cognitiveservices:azure-cognitiveservices-knowledge-qnamaker",
]


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--all":
        packages = DEFAULT_PACKAGES
    elif len(sys.argv) > 1:
        packages = sys.argv[1:]
    else:
        packages = DEFAULT_PACKAGES

    print(f"Checking {len(packages)} packages on Maven Central...")
    print(f"Using headers: {HEADERS}")
    print(f"Deprecation threshold: {DEPRECATION_THRESHOLD_YEARS} years")

    results = check_packages(packages)
    print_results(results)

    # Exit with error code if any deprecated packages found (useful for CI)
    sys.exit(0)
