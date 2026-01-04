# TenantControllerApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**canAccessTenant**](TenantControllerApi.md#canAccessTenant) | **GET** /tenant/access | Validate if user can access tenant [Authorization: no-auth] |
| [**createTenant**](TenantControllerApi.md#createTenant) | **POST** /tenantadmin | Creates a new tenant [Authorization: Role: tenant-admin] |
| [**getAllTenants**](TenantControllerApi.md#getAllTenants) | **GET** /tenant | Get all tenants |
| [**getAllTenantsWithAdminData**](TenantControllerApi.md#getAllTenantsWithAdminData) | **GET** /tenantadmin | Get all tenants with admin data [Authorization: Role: tenant-admin] |
| [**getMultilingualTenantById**](TenantControllerApi.md#getMultilingualTenantById) | **GET** /tenantadmin/{id} | Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin] |
| [**getRestrictedSingleTenantData**](TenantControllerApi.md#getRestrictedSingleTenantData) | **GET** /tenant/public/single | Gets a tenant public information [Authorization: no-auth] |
| [**getRestrictedTenantDataBySubdomain**](TenantControllerApi.md#getRestrictedTenantDataBySubdomain) | **GET** /tenant/public/{subdomain} | Gets a tenant public information [Authorization: no-auth] |
| [**getRestrictedTenantDataByTenantId**](TenantControllerApi.md#getRestrictedTenantDataByTenantId) | **GET** /tenant/public/id/{tenantId} | Gets a tenant public information [Authorization: no-auth] |
| [**getTenantById**](TenantControllerApi.md#getTenantById) | **GET** /tenant/{id} | Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin] |
| [**searchTenants**](TenantControllerApi.md#searchTenants) | **GET** /tenantadmin/search | Get tenants matching the given query [Auth: tenant-admin] |
| [**updateTenant**](TenantControllerApi.md#updateTenant) | **PUT** /tenantadmin/{id} | Updates a tenant [Authorization: Role: tenant-admin or single-tenant-admin] |



## canAccessTenant

> canAccessTenant()

Validate if user can access tenant [Authorization: no-auth]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        try {
            apiInstance.canAccessTenant();
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#canAccessTenant");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Successful operation |  -  |
| **401** | UNAUTHORIZED - no/invalid permission for current tenant |  -  |
| **404** | Not found |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## createTenant

> MultilingualTenantDTO createTenant(multilingualTenantDTO)

Creates a new tenant [Authorization: Role: tenant-admin]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        MultilingualTenantDTO multilingualTenantDTO = new MultilingualTenantDTO(); // MultilingualTenantDTO | 
        try {
            MultilingualTenantDTO result = apiInstance.createTenant(multilingualTenantDTO);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#createTenant");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **multilingualTenantDTO** | [**MultilingualTenantDTO**](MultilingualTenantDTO.md)|  | [optional] |

### Return type

[**MultilingualTenantDTO**](MultilingualTenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK - successful operation |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **401** | UNAUTHORIZED - no/invalid Keycloak token |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## getAllTenants

> List&lt;BasicTenantLicensingDTO&gt; getAllTenants()

Get all tenants

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        try {
            List<BasicTenantLicensingDTO> result = apiInstance.getAllTenants();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#getAllTenants");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**List&lt;BasicTenantLicensingDTO&gt;**](BasicTenantLicensingDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK - successful operation |  -  |
| **204** | NO CONTENT - no content found |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **401** | UNAUTHORIZED - no/invalid Keycloak token |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## getAllTenantsWithAdminData

> List&lt;AdminTenantDTO&gt; getAllTenantsWithAdminData()

Get all tenants with admin data [Authorization: Role: tenant-admin]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        try {
            List<AdminTenantDTO> result = apiInstance.getAllTenantsWithAdminData();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#getAllTenantsWithAdminData");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**List&lt;AdminTenantDTO&gt;**](AdminTenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK - successful operation |  -  |
| **204** | NO CONTENT - no content found |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **401** | UNAUTHORIZED - no/invalid Keycloak token |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## getMultilingualTenantById

> MultilingualTenantDTO getMultilingualTenantById(id)

Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        Long id = 56L; // Long | Tenant ID
        try {
            MultilingualTenantDTO result = apiInstance.getMultilingualTenantById(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#getMultilingualTenantById");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**| Tenant ID | |

### Return type

[**MultilingualTenantDTO**](MultilingualTenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful operation |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **401** | UNAUTHORIZED - no/invalid Keycloak token |  -  |
| **404** | Not found |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## getRestrictedSingleTenantData

> RestrictedTenantDTO getRestrictedSingleTenantData()

Gets a tenant public information [Authorization: no-auth]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        try {
            RestrictedTenantDTO result = apiInstance.getRestrictedSingleTenantData();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#getRestrictedSingleTenantData");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**RestrictedTenantDTO**](RestrictedTenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful operation |  -  |
| **404** | Not found |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## getRestrictedTenantDataBySubdomain

> RestrictedTenantDTO getRestrictedTenantDataBySubdomain(subdomain, tenantId)

Gets a tenant public information [Authorization: no-auth]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        String subdomain = "subdomain_example"; // String | Subdomain
        Long tenantId = 56L; // Long | Tenant Id
        try {
            RestrictedTenantDTO result = apiInstance.getRestrictedTenantDataBySubdomain(subdomain, tenantId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#getRestrictedTenantDataBySubdomain");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **subdomain** | **String**| Subdomain | |
| **tenantId** | [**Long**](.md)| Tenant Id | [optional] |

### Return type

[**RestrictedTenantDTO**](RestrictedTenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful operation |  -  |
| **404** | Not found |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## getRestrictedTenantDataByTenantId

> RestrictedTenantDTO getRestrictedTenantDataByTenantId(tenantId)

Gets a tenant public information [Authorization: no-auth]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        Long tenantId = 56L; // Long | Tenant ID
        try {
            RestrictedTenantDTO result = apiInstance.getRestrictedTenantDataByTenantId(tenantId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#getRestrictedTenantDataByTenantId");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tenantId** | [**Long**](.md)| Tenant ID | |

### Return type

[**RestrictedTenantDTO**](RestrictedTenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful operation |  -  |
| **404** | Not found |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## getTenantById

> TenantDTO getTenantById(id)

Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        Long id = 56L; // Long | Tenant ID
        try {
            TenantDTO result = apiInstance.getTenantById(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#getTenantById");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**| Tenant ID | |

### Return type

[**TenantDTO**](TenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful operation |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **401** | UNAUTHORIZED - no/invalid Keycloak token |  -  |
| **404** | Not found |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## searchTenants

> TenantsSearchResultDTO searchTenants(query, page, perPage, field, order)

Get tenants matching the given query [Auth: tenant-admin]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        String query = "query_example"; // String | URL-encoded infix to search for name or id. A non-encoded star symbol searches for all.
        Integer page = 1; // Integer | Page number (first page = 1)
        Integer perPage = 10; // Integer | Number of items returned per page
        String field = "NAME"; // String | field to sort by
        String order = "ASC"; // String | sort order
        try {
            TenantsSearchResultDTO result = apiInstance.searchTenants(query, page, perPage, field, order);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#searchTenants");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **query** | **String**| URL-encoded infix to search for name or id. A non-encoded star symbol searches for all. | |
| **page** | **Integer**| Page number (first page &#x3D; 1) | [optional] [default to 1] |
| **perPage** | **Integer**| Number of items returned per page | [optional] [default to 10] |
| **field** | **String**| field to sort by | [optional] [default to NAME] [enum: NAME, ID] |
| **order** | **String**| sort order | [optional] [default to ASC] [enum: ASC, DESC] |

### Return type

[**TenantsSearchResultDTO**](TenantsSearchResultDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/hal+json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK - successful operation |  -  |
| **400** | BAD REQUEST - invalid/incomplete request or body object |  -  |
| **401** | UNAUTHORIZED - no/invalid role/authorization |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |


## updateTenant

> MultilingualTenantDTO updateTenant(id, multilingualTenantDTO)

Updates a tenant [Authorization: Role: tenant-admin or single-tenant-admin]

### Example

```java
// Import classes:
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiException;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.Configuration;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.models.*;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        TenantControllerApi apiInstance = new TenantControllerApi(defaultClient);
        Long id = 56L; // Long | Tenant ID
        MultilingualTenantDTO multilingualTenantDTO = new MultilingualTenantDTO(); // MultilingualTenantDTO | 
        try {
            MultilingualTenantDTO result = apiInstance.updateTenant(id, multilingualTenantDTO);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TenantControllerApi#updateTenant");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**| Tenant ID | |
| **multilingualTenantDTO** | [**MultilingualTenantDTO**](MultilingualTenantDTO.md)|  | [optional] |

### Return type

[**MultilingualTenantDTO**](MultilingualTenantDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful operation |  -  |
| **401** | UNAUTHORIZED - no/invalid Keycloak token |  -  |
| **409** | CONFLICT - unique constraint validation fails |  -  |
| **500** | INTERNAL SERVER ERROR - server encountered unexpected condition |  -  |

