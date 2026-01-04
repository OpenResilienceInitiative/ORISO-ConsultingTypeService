# ConsultingTypeAdminControllerApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getConsultingTypes**](ConsultingTypeAdminControllerApi.md#getConsultingTypes) | **GET** /consultingtypeadmin/consultingtypes | Returns the full list of consulting types |


<a name="getConsultingTypes"></a>
# **getConsultingTypes**
> ConsultingTypeAdminResultDTO getConsultingTypes(page, perPage)

Returns the full list of consulting types

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **page** | **Integer**| Number of page where to start in the query (1 &#x3D; first page) | [default to null] |
| **perPage** | **Integer**| Number of items which are being returned per page | [default to null] |

### Return type

[**ConsultingTypeAdminResultDTO**](../model/ConsultingTypeAdminResultDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/hal+json

