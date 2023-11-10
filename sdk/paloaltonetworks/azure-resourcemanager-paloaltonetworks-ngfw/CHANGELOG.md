# Release History

## 1.1.0-beta.1 (2023-11-10)

- Azure Resource Manager PaloAlto Networks Ngfw client library for Java. This package contains Microsoft Azure SDK for PaloAlto Networks Ngfw Management SDK.  Package tag package-2023-09-01. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

### Breaking Changes

#### `models.LocalRulestacks` was modified

* `listCountriesWithResponse(java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was removed
* `listAppIdsWithResponse(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was removed
* `listPredefinedUrlCategoriesWithResponse(java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was removed
* `models.CountriesResponse listCountries(java.lang.String,java.lang.String)` -> `com.azure.core.http.rest.PagedIterable listCountries(java.lang.String,java.lang.String)`
* `models.PredefinedUrlCategoriesResponse listPredefinedUrlCategories(java.lang.String,java.lang.String)` -> `com.azure.core.http.rest.PagedIterable listPredefinedUrlCategories(java.lang.String,java.lang.String)`
* `models.ListAppIdResponse listAppIds(java.lang.String,java.lang.String)` -> `com.azure.core.http.rest.PagedIterable listAppIds(java.lang.String,java.lang.String)`

#### `models.PredefinedUrlCategory` was modified

* `withAction(java.lang.String)` was removed
* `java.lang.String action()` -> `java.lang.String action()`
* `java.lang.String name()` -> `java.lang.String name()`
* `validate()` was removed
* `withName(java.lang.String)` was removed

#### `models.LocalRulestackResource` was modified

* `listAppIdsWithResponse(java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was removed
* `models.PredefinedUrlCategoriesResponse listPredefinedUrlCategories()` -> `com.azure.core.http.rest.PagedIterable listPredefinedUrlCategories()`
* `listPredefinedUrlCategoriesWithResponse(java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was removed
* `models.CountriesResponse listCountries()` -> `com.azure.core.http.rest.PagedIterable listCountries()`
* `listCountriesWithResponse(java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was removed
* `models.ListAppIdResponse listAppIds()` -> `com.azure.core.http.rest.PagedIterable listAppIds()`

#### `models.Country` was modified

* `withCode(java.lang.String)` was removed
* `withDescription(java.lang.String)` was removed
* `java.lang.String code()` -> `java.lang.String code()`
* `java.lang.String description()` -> `java.lang.String description()`
* `validate()` was removed

### Features Added

#### `models.LocalRulestacks` was modified

* `listAppIds(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was added
* `listPredefinedUrlCategories(java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was added
* `listCountries(java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was added

#### `models.PredefinedUrlCategory` was modified

* `innerModel()` was added

#### `models.LocalRulestackResource` was modified

* `listCountries(java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was added
* `listAppIds(java.lang.String,java.lang.String,java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was added
* `listPredefinedUrlCategories(java.lang.String,java.lang.Integer,com.azure.core.util.Context)` was added

#### `models.NetworkProfile` was modified

* `trustedRanges()` was added
* `withTrustedRanges(java.util.List)` was added

#### `models.Country` was modified

* `innerModel()` was added

## 1.0.0 (2023-07-14)

- Azure Resource Manager PaloAlto Networks Ngfw client library for Java. This package contains Microsoft Azure SDK for PaloAlto Networks Ngfw Management SDK.  Package tag package-2022-08-29. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

## 1.0.0-beta.1 (2023-05-04)

- Azure Resource Manager PaloAlto Networks Ngfw client library for Java. This package contains Microsoft Azure SDK for PaloAlto Networks Ngfw Management SDK.  Package tag package-2022-08-29-preview. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).
