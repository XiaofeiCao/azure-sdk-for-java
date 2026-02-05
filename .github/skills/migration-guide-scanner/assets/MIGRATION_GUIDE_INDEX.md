# Azure SDK for Java Migration Guide Index

> **Generated on: February 5, 2026** (Last Maven scan performed)
>
> This index lists all Track 1 (legacy) to Track 2 (current) package migrations, including both documented migrations with guides and known package equivalents.

## Maven Central Scan Summary

| Category                                  | Count |
| ----------------------------------------- | ----- |
| Active packages (do not migrate)          | 22    |
| Deprecated packages (recommend migration) | 53    |
| Not found on Maven                        | 14    |

## Data Plane Libraries (With Migration Guides)

### Messaging Services

| Track 1 Package (Legacy)                                 | Track 2 Package (Current)            | Migration Guide                                                                                                                                                                                     |
| -------------------------------------------------------- | ------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| com.microsoft.azure:azure-servicebus                     | com.azure:azure-messaging-servicebus | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/servicebus/azure-messaging-servicebus/migration-guide.md) / [aka.ms](https://aka.ms/azsdk/java/migrate/sb) |
| com.microsoft.azure:azure-eventhubs, azure-eventhubs-eph | com.azure:azure-messaging-eventhubs  | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/eventhubs/azure-messaging-eventhubs/migration-guide.md) / [aka.ms](https://aka.ms/azsdk/java/migrate/eh)   |
| com.microsoft.azure:azure-eventgrid                      | com.azure:azure-messaging-eventgrid  | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/eventgrid/azure-messaging-eventgrid/migration-guide.md)                                                    |

### Storage Services

| Track 1 Package (Legacy)                       | Track 2 Package (Current)                 | Migration Guide                                                                                                                             |
| ---------------------------------------------- | ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| com.microsoft.azure:azure-storage (v8.x)       | com.azure:azure-storage-blob              | [V8_V12.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-blob/migrationGuides/V8_V12.md)       |
| com.microsoft.azure:azure-storage (v10.x/11.x) | com.azure:azure-storage-blob              | [V10_V12.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-blob/migrationGuides/V10_V12.md)     |
| com.microsoft.azure:azure-storage (v8.x)       | com.azure:azure-storage-queue             | [V8_V12.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-queue/migrationGuides/V8_V12.md)      |
| com.microsoft.azure:azure-storage (v8.x)       | com.azure:azure-storage-file-share        | [V8_V12.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-file-share/migrationGuides/V8_V12.md) |
| com.microsoft.azure:azure-storage (v8.x)       | com.azure:azure-storage-blob-batch        | No dedicated guide                                                                                                                          |
| com.microsoft.azure:azure-storage (v8.x)       | com.azure:azure-storage-blob-cryptography | No dedicated guide                                                                                                                          |

### Key Vault Services

| Track 1 Package (Legacy)           | Track 2 Package (Current)                      | Migration Guide                                                                                                                                            |
| ---------------------------------- | ---------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| com.microsoft.azure:azure-keyvault | com.azure:azure-security-keyvault-secrets      | [migration_guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-secrets/migration_guide.md)      |
| com.microsoft.azure:azure-keyvault | com.azure:azure-security-keyvault-keys         | [migration_guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-keys/migration_guide.md)         |
| com.microsoft.azure:azure-keyvault | com.azure:azure-security-keyvault-certificates | [migration_guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-certificates/migration_guide.md) |

### Batch Services

| Track 1 Package (Legacy)                  | Track 2 Package (Current)     | Migration Guide                                                                                                                      |
| ----------------------------------------- | ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| com.microsoft.azure:microsoft-azure-batch | com.azure:azure-compute-batch | [MigrationGuide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/batch/azure-compute-batch/MigrationGuide.md) |

### AI Services

| Track 1 Package (Legacy)                 | Track 2 Package (Current)                | Migration Guide                                                                                                                                                 |
| ---------------------------------------- | ---------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| com.azure:azure-ai-formrecognizer (v3.x) | com.azure:azure-ai-documentintelligence  | [MIGRATION_GUIDE.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/documentintelligence/azure-ai-documentintelligence/MIGRATION_GUIDE.md) |
| com.azure:azure-ai-formrecognizer (v3.x) | com.azure:azure-ai-formrecognizer (v4.x) | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/formrecognizer/azure-ai-formrecognizer/migration-guide.md)             |
| com.azure:azure-ai-openai                | openai-java (OpenAI Official SDK)        | [MIGRATION.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/openai/azure-ai-openai-stainless/MIGRATION.md)                               |

### Monitor Services

| Track 1 Package (Legacy)                       | Track 2 Package (Current)                           | Migration Guide                                                                                                                                  |
| ---------------------------------------------- | --------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| com.azure:azure-monitor-opentelemetry-exporter | com.azure:azure-monitor-opentelemetry-autoconfigure | [MIGRATION.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/azure-monitor-opentelemetry-exporter/MIGRATION.md)    |
| com.azure:azure-monitor-query                  | com.azure:azure-monitor-query-logs                  | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/azure-monitor-query-logs/migration-guide.md)    |
| com.azure:azure-monitor-query                  | com.azure:azure-monitor-query-metrics               | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/azure-monitor-query-metrics/migration-guide.md) |
| —                                              | —                                                   | [migration-guide-query.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/migration-guide-query.md) (overview)      |

### Cosmos DB Spark Connector

| Track 1 Package (Legacy)              | Track 2 Package (Current)   | Migration Guide                                                                                                                                 |
| ------------------------------------- | --------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| Cosmos DB Spark Connector (Spark 2.4) | azure-cosmos-spark_3        | [migration.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/cosmos/azure-cosmos-spark_3/docs/migration.md)               |
| azure-cosmos-spark_3 (older)          | azure-cosmos-spark_3-1_2-12 | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/cosmos/azure-cosmos-spark_3-1_2-12/migration-guide.md) |
| azure-cosmos-spark_3-1                | azure-cosmos-spark_3-2_2-12 | [migration-guide.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/cosmos/azure-cosmos-spark_3-2_2-12/migration-guide.md) |

## Management Libraries (With Migration Guide)

| Track 1 Package (Legacy)                          | Track 2 Package (Current)                  | Migration Guide                                                                                                                   |
| ------------------------------------------------- | ------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------- |
| com.microsoft.azure.management:\*                 | com.azure.resourcemanager:\*               | [MIGRATION_GUIDE.md](https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/resourcemanager/docs/MIGRATION_GUIDE.md) |
| com.azure:azure-resourcemanager-playwrighttesting | com.azure:azure-resourcemanager-playwright | [aka.ms](https://aka.ms/mpt/migration-guidance)                                                                                   |

---

## Known Track 1 → Track 2 Mappings (Without Dedicated Migration Guides)

These are additional known package equivalents that don't have dedicated migration guide files in the repository.

### Data Plane Libraries

| Track 1 Package (Legacy)                                                       | Track 2 Package (Current)                                     | Notes                                  |
| ------------------------------------------------------------------------------ | ------------------------------------------------------------- | -------------------------------------- |
| com.microsoft.azure:azure-eventhubs-eph                                        | com.azure:azure-messaging-eventhubs-checkpointstore-blob      | Event Processor Host replacement       |
| com.microsoft.azure:azure-cosmos                                               | com.azure:azure-cosmos                                        | Cosmos DB (same name, different group) |
| com.microsoft.azure:azure-cosmosdb                                             | com.azure:azure-cosmos                                        | Cosmos DB                              |
| com.microsoft.azure:azure-documentdb                                           | com.azure:azure-cosmos                                        | Document DB (predecessor)              |
| com.microsoft.azure:azure-documentdb-rx                                        | com.azure:azure-cosmos                                        | Reactive Document DB                   |
| com.microsoft.azure:azure-client-runtime                                       | com.azure:azure-core                                          | Core runtime                           |
| com.microsoft.azure:azure-client-authentication                                | com.azure:azure-identity                                      | Authentication                         |
| com.microsoft.azure:adal4j                                                     | com.azure:azure-identity                                      | ADAL → MSAL/azure-identity             |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-textanalytics    | com.azure:azure-ai-textanalytics                              | Text Analytics                         |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-computervision   | com.azure:azure-ai-vision-imageanalysis                       | Computer Vision                        |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-anomalydetector  | com.azure:azure-ai-anomalydetector                            | Anomaly Detector                       |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-contentmoderator | com.azure:azure-ai-contentsafety                              | Content Moderator → Content Safety     |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-face             | com.azure:azure-ai-vision-face                                | Face API                               |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-luis-authoring   | com.azure:azure-ai-language-\*                                | LUIS → Language services               |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-qnamaker         | com.azure:azure-ai-language-questionanswering                 | QnA Maker → Question Answering         |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-personalizer     | com.azure:azure-ai-personalizer                               | Personalizer                           |
| com.microsoft.azure.cognitiveservices:azure-cognitiveservices-translatortext   | com.azure:azure-ai-translation-text                           | Translator Text                        |
| com.microsoft.azure:azure-media                                                | com.azure.resourcemanager:azure-resourcemanager-mediaservices | Media Services (deprecated)            |
| com.microsoft.azure:azure-search                                               | com.azure:azure-search-documents                              | Azure Cognitive Search                 |
| com.microsoft.azure:azure-mgmt-iothub                                          | com.azure.resourcemanager:azure-resourcemanager-iothub        | IoT Hub                                |

### Management Libraries (azure-mgmt-_ → azure-resourcemanager-_)

All `com.microsoft.azure:azure-mgmt-*` packages map to `com.azure.resourcemanager:azure-resourcemanager-*`:

| Track 1 Package               | Track 2 Package                          |
| ----------------------------- | ---------------------------------------- |
| azure-mgmt-resources          | azure-resourcemanager-resources          |
| azure-mgmt-compute            | azure-resourcemanager-compute            |
| azure-mgmt-storage            | azure-resourcemanager-storage            |
| azure-mgmt-network            | azure-resourcemanager-network            |
| azure-mgmt-keyvault           | azure-resourcemanager-keyvault           |
| azure-mgmt-sql                | azure-resourcemanager-sql                |
| azure-mgmt-cosmosdb           | azure-resourcemanager-cosmos             |
| azure-mgmt-eventhub           | azure-resourcemanager-eventhubs          |
| azure-mgmt-servicebus         | azure-resourcemanager-servicebus         |
| azure-mgmt-appservice         | azure-resourcemanager-appservice         |
| azure-mgmt-containerregistry  | azure-resourcemanager-containerregistry  |
| azure-mgmt-containerservice   | azure-resourcemanager-containerservice   |
| azure-mgmt-dns                | azure-resourcemanager-dns                |
| azure-mgmt-monitor            | azure-resourcemanager-monitor            |
| azure-mgmt-redis              | azure-resourcemanager-redis              |
| azure-mgmt-trafficmanager     | azure-resourcemanager-trafficmanager     |
| azure-mgmt-cdn                | azure-resourcemanager-cdn                |
| azure-mgmt-batch              | azure-resourcemanager-batch              |
| azure-mgmt-cognitiveservices  | azure-resourcemanager-cognitiveservices  |
| azure-mgmt-datalake-analytics | azure-resourcemanager-datalakeanalytics  |
| azure-mgmt-datalake-store     | azure-resourcemanager-datalakestore      |
| azure-mgmt-hdinsight          | azure-resourcemanager-hdinsight          |
| azure-mgmt-locks              | azure-resourcemanager-resources (merged) |
| azure-mgmt-policy             | azure-resourcemanager-resources (merged) |
| azure-mgmt-privatedns         | azure-resourcemanager-privatedns         |
| azure-mgmt-search             | azure-resourcemanager-search             |
| azure-mgmt-signalr            | azure-resourcemanager-signalr            |
| azure-mgmt-streamanalytics    | azure-resourcemanager-streamanalytics    |

---

## Summary

| Category              | With Guide | Without Guide | Total   |
| --------------------- | ---------- | ------------- | ------- |
| Messaging Services    | 3          | 1             | 4       |
| Storage Services      | 4          | 0             | 4       |
| Key Vault Services    | 3          | 0             | 3       |
| Batch Services        | 1          | 0             | 1       |
| AI/Cognitive Services | 3          | 15            | 18      |
| Monitor Services      | 4          | 1             | 5       |
| Cosmos DB / Spark     | 3          | 4             | 7       |
| Management Libraries  | 1          | 28+           | 29+     |
| Core/Identity         | 0          | 4             | 4       |
| **Total**             | **22**     | **53+**       | **75+** |

## Excluded

The following migration guides exist but are excluded from this index:

| Package                      | Reason                                                                  |
| ---------------------------- | ----------------------------------------------------------------------- |
| azure-core → azure-core-v2   | Not ready for production (sdk/tools/azure-openrewrite/MIGRATION.md)     |
| Spring Cloud Azure 3.x → 4.x | Handled separately (https://aka.ms/spring/docs#migration-guide-for-4-0) |

## Actively Maintained `com.microsoft.azure` Packages (NOT Deprecated)

The following packages use the `com.microsoft.azure` group ID but are **actively maintained** and should NOT be migrated away from. Check https://repo1.maven.org/maven2/com/microsoft/azure/ for latest release dates.

| Package                                          | Last Updated | Notes                                                           |
| ------------------------------------------------ | ------------ | --------------------------------------------------------------- |
| com.microsoft.azure:applicationinsights-core     | 2026-01-24   | Application Insights core - actively maintained                 |
| com.microsoft.azure:applicationinsights-web      | 2026-01-24   | Application Insights web - actively maintained                  |
| com.microsoft.azure:qpid-proton-j-extensions     | 2025-10-24   | AMQP extensions - used by messaging libraries                   |
| com.microsoft.azure:msal4j                       | 2025-09-12   | Microsoft Authentication Library for Java - actively maintained |
| com.microsoft.azure:azure-batch                  | 2024-09-11   | Batch - still actively maintained                               |
| com.microsoft.azure:msal4j-persistence-extension | 2024-04-05   | MSAL persistence extension - actively maintained                |

Additionally, these `com.microsoft.azure.cognitiveservices` packages are still receiving updates (as of 2025-04-09):

- azure-cognitiveservices-websearch, azure-cognitiveservices-visualsearch, azure-cognitiveservices-videosearch
- azure-cognitiveservices-spellcheck, azure-cognitiveservices-qnamaker, azure-cognitiveservices-newssearch
- azure-cognitiveservices-luis-runtime, azure-cognitiveservices-luis-authoring, azure-cognitiveservices-imagesearch
- azure-cognitiveservices-entitysearch, azure-cognitiveservices-customvision-training, azure-cognitiveservices-customvision-prediction
- azure-cognitiveservices-customsearch, azure-cognitiveservices-contentmoderator, azure-cognitiveservices-computervision
- azure-cognitiveservices-autosuggest

> **Note**: Even though some cognitive services packages are actively maintained, Track 2 equivalents are recommended for new projects.

> **Rule**: Only include `com.microsoft.azure` packages in migration recommendations if they have NOT been released within the last 2 years on Maven Central.
>
> **Validation script**: Run `python .github/skills/migration-guide-scanner/scripts/check_maven_packages.py` to verify package status.

## File Locations Reference

All migration guide files found in the repository:

### Dedicated Migration Files (24 files)

| File                                                                      | GitHub Raw URL                                                                                                                            |
| ------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| sdk/batch/azure-compute-batch/MigrationGuide.md                           | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/batch/azure-compute-batch/MigrationGuide.md                           |
| sdk/servicebus/azure-messaging-servicebus/migration-guide.md              | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/servicebus/azure-messaging-servicebus/migration-guide.md              |
| sdk/eventhubs/azure-messaging-eventhubs/migration-guide.md                | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/eventhubs/azure-messaging-eventhubs/migration-guide.md                |
| sdk/eventgrid/azure-messaging-eventgrid/migration-guide.md                | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/eventgrid/azure-messaging-eventgrid/migration-guide.md                |
| sdk/storage/azure-storage-blob/migrationGuides/V8_V12.md                  | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-blob/migrationGuides/V8_V12.md                  |
| sdk/storage/azure-storage-blob/migrationGuides/V10_V12.md                 | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-blob/migrationGuides/V10_V12.md                 |
| sdk/storage/azure-storage-queue/migrationGuides/V8_V12.md                 | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-queue/migrationGuides/V8_V12.md                 |
| sdk/storage/azure-storage-file-share/migrationGuides/V8_V12.md            | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/storage/azure-storage-file-share/migrationGuides/V8_V12.md            |
| sdk/keyvault/azure-security-keyvault-secrets/migration_guide.md           | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-secrets/migration_guide.md           |
| sdk/keyvault/azure-security-keyvault-keys/migration_guide.md              | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-keys/migration_guide.md              |
| sdk/keyvault/azure-security-keyvault-certificates/migration_guide.md      | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/keyvault/azure-security-keyvault-certificates/migration_guide.md      |
| sdk/documentintelligence/azure-ai-documentintelligence/MIGRATION_GUIDE.md | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/documentintelligence/azure-ai-documentintelligence/MIGRATION_GUIDE.md |
| sdk/formrecognizer/azure-ai-formrecognizer/migration-guide.md             | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/formrecognizer/azure-ai-formrecognizer/migration-guide.md             |
| sdk/openai/azure-ai-openai-stainless/MIGRATION.md                         | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/openai/azure-ai-openai-stainless/MIGRATION.md                         |
| sdk/monitor/azure-monitor-opentelemetry-exporter/MIGRATION.md             | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/azure-monitor-opentelemetry-exporter/MIGRATION.md             |
| sdk/monitor/azure-monitor-query/migration-guide.md                        | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/azure-monitor-query/migration-guide.md                        |
| sdk/monitor/azure-monitor-query-logs/migration-guide.md                   | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/azure-monitor-query-logs/migration-guide.md                   |
| sdk/monitor/azure-monitor-query-metrics/migration-guide.md                | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/azure-monitor-query-metrics/migration-guide.md                |
| sdk/monitor/migration-guide-query.md                                      | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/monitor/migration-guide-query.md                                      |
| sdk/cosmos/azure-cosmos-spark_3/docs/migration.md                         | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/cosmos/azure-cosmos-spark_3/docs/migration.md                         |
| sdk/cosmos/azure-cosmos-spark_3-1_2-12/migration-guide.md                 | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/cosmos/azure-cosmos-spark_3-1_2-12/migration-guide.md                 |
| sdk/cosmos/azure-cosmos-spark_3-2_2-12/migration-guide.md                 | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/cosmos/azure-cosmos-spark_3-2_2-12/migration-guide.md                 |
| sdk/resourcemanager/docs/MIGRATION_GUIDE.md                               | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/resourcemanager/docs/MIGRATION_GUIDE.md                               |
| sdk/purview/azure-analytics-purview-datamap/MigrationGuide.md             | https://raw.githubusercontent.com/Azure/azure-sdk-for-java/main/sdk/purview/azure-analytics-purview-datamap/MigrationGuide.md             |

### External Migration Guide Links (aka.ms)

| Service            | URL                                   |
| ------------------ | ------------------------------------- |
| Service Bus        | https://aka.ms/azsdk/java/migrate/sb  |
| Event Hubs         | https://aka.ms/azsdk/java/migrate/eh  |
| Playwright Testing | https://aka.ms/mpt/migration-guidance |
