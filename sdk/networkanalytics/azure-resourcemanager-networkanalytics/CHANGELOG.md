# Release History

## 1.1.0-beta.1 (Unreleased)

### Breaking Changes

* `models.ManagedServiceIdentityType` was removed

* `models.IpRules` was removed

* `models.OperationListResult` was removed

* `models.ManagedServiceIdentity` was removed

#### `models.DataProduct` was modified

* `models.ManagedServiceIdentity identity()` -> `models.ManagedIdentityProperties identity()`

#### `models.DataTypeListResult` was modified

* `withNextLink(java.lang.String)` was removed

#### `models.DataProductUpdate` was modified

* `models.ManagedServiceIdentity identity()` -> `models.ManagedIdentityProperties identity()`
* `withIdentity(models.ManagedServiceIdentity)` was removed

#### `models.UserAssignedIdentity` was modified

* `java.util.UUID clientId()` -> `java.lang.String clientId()`
* `java.util.UUID principalId()` -> `java.lang.String principalId()`

#### `models.DataProduct$Update` was modified

* `withIdentity(models.ManagedServiceIdentity)` was removed

#### `models.DataProductsCatalogListResult` was modified

* `withNextLink(java.lang.String)` was removed

#### `NetworkAnalyticsManager` was modified

* `fluent.NetworkAnalyticsMgmtClient serviceClient()` -> `fluent.NetworkAnalyticsClient serviceClient()`

#### `models.DataProductListResult` was modified

* `withNextLink(java.lang.String)` was removed

#### `models.DataProduct$Definition` was modified

* `withIdentity(models.ManagedServiceIdentity)` was removed

### Features Added

* `models.IPRules` was added

* `models.PagedOperation` was added

* `models.ManagedIdentityProperties` was added

* `models.ManagedIdentityType` was added

#### `models.OperationDisplay` was modified

* `withDescription(java.lang.String)` was added
* `withProvider(java.lang.String)` was added
* `withResource(java.lang.String)` was added
* `withOperation(java.lang.String)` was added

#### `models.DataProductUpdate` was modified

* `withIdentity(models.ManagedIdentityProperties)` was added

#### `models.UserAssignedIdentity` was modified

* `withPrincipalId(java.lang.String)` was added
* `withClientId(java.lang.String)` was added

#### `models.DataProduct$Update` was modified

* `withIdentity(models.ManagedIdentityProperties)` was added

#### `models.DataProduct$Definition` was modified

* `withIdentity(models.ManagedIdentityProperties)` was added

## 1.0.0 (2024-01-24)

- Azure Resource Manager Network Analytics client library for Java. This package contains Microsoft Azure SDK for Network Analytics Management SDK.  Package tag package-2023-11-15. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

## 1.0.0-beta.1 (2023-11-28)

- Azure Resource Manager Network Analytics client library for Java. This package contains Microsoft Azure SDK for Network Analytics Management SDK.  Package tag package-2023-11-15. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).
