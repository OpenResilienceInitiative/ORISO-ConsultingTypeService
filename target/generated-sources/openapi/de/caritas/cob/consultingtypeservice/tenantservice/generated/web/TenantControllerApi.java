package de.caritas.cob.consultingtypeservice.tenantservice.generated.web;

import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;

import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.AdminTenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.BasicTenantLicensingDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.MultilingualTenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.TenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.TenantsSearchResultDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-12-25T23:22:20.429782645Z[Etc/UTC]")
@Component("de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi")
public class TenantControllerApi {
    private ApiClient apiClient;

    public TenantControllerApi() {
        this(new ApiClient());
    }

    @Autowired
    public TenantControllerApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Validate if user can access tenant [Authorization: no-auth]
     * 
     * <p><b>204</b> - Successful operation
     * <p><b>401</b> - UNAUTHORIZED - no/invalid permission for current tenant
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void canAccessTenant() throws RestClientException {
        canAccessTenantWithHttpInfo();
    }

    /**
     * Validate if user can access tenant [Authorization: no-auth]
     * 
     * <p><b>204</b> - Successful operation
     * <p><b>401</b> - UNAUTHORIZED - no/invalid permission for current tenant
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> canAccessTenantWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/tenant/access", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Creates a new tenant [Authorization: Role: tenant-admin]
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param multilingualTenantDTO  (optional)
     * @return MultilingualTenantDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public MultilingualTenantDTO createTenant(MultilingualTenantDTO multilingualTenantDTO) throws RestClientException {
        return createTenantWithHttpInfo(multilingualTenantDTO).getBody();
    }

    /**
     * Creates a new tenant [Authorization: Role: tenant-admin]
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param multilingualTenantDTO  (optional)
     * @return ResponseEntity&lt;MultilingualTenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<MultilingualTenantDTO> createTenantWithHttpInfo(MultilingualTenantDTO multilingualTenantDTO) throws RestClientException {
        Object localVarPostBody = multilingualTenantDTO;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<MultilingualTenantDTO> localReturnType = new ParameterizedTypeReference<MultilingualTenantDTO>() {};
        return apiClient.invokeAPI("/tenantadmin", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Get all tenants
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>204</b> - NO CONTENT - no content found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @return List&lt;BasicTenantLicensingDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<BasicTenantLicensingDTO> getAllTenants() throws RestClientException {
        return getAllTenantsWithHttpInfo().getBody();
    }

    /**
     * Get all tenants
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>204</b> - NO CONTENT - no content found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @return ResponseEntity&lt;List&lt;BasicTenantLicensingDTO&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<BasicTenantLicensingDTO>> getAllTenantsWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<List<BasicTenantLicensingDTO>> localReturnType = new ParameterizedTypeReference<List<BasicTenantLicensingDTO>>() {};
        return apiClient.invokeAPI("/tenant", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Get all tenants with admin data [Authorization: Role: tenant-admin]
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>204</b> - NO CONTENT - no content found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @return List&lt;AdminTenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<AdminTenantDTO> getAllTenantsWithAdminData() throws RestClientException {
        return getAllTenantsWithAdminDataWithHttpInfo().getBody();
    }

    /**
     * Get all tenants with admin data [Authorization: Role: tenant-admin]
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>204</b> - NO CONTENT - no content found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @return ResponseEntity&lt;List&lt;AdminTenantDTO&gt;&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<List<AdminTenantDTO>> getAllTenantsWithAdminDataWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<List<AdminTenantDTO>> localReturnType = new ParameterizedTypeReference<List<AdminTenantDTO>>() {};
        return apiClient.invokeAPI("/tenantadmin", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>404</b> - Not found
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param id Tenant ID (required)
     * @return MultilingualTenantDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public MultilingualTenantDTO getMultilingualTenantById(Long id) throws RestClientException {
        return getMultilingualTenantByIdWithHttpInfo(id).getBody();
    }

    /**
     * Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>404</b> - Not found
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param id Tenant ID (required)
     * @return ResponseEntity&lt;MultilingualTenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<MultilingualTenantDTO> getMultilingualTenantByIdWithHttpInfo(Long id) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling getMultilingualTenantById");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<MultilingualTenantDTO> localReturnType = new ParameterizedTypeReference<MultilingualTenantDTO>() {};
        return apiClient.invokeAPI("/tenantadmin/{id}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Gets a tenant public information [Authorization: no-auth]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @return RestrictedTenantDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RestrictedTenantDTO getRestrictedSingleTenantData() throws RestClientException {
        return getRestrictedSingleTenantDataWithHttpInfo().getBody();
    }

    /**
     * Gets a tenant public information [Authorization: no-auth]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @return ResponseEntity&lt;RestrictedTenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RestrictedTenantDTO> getRestrictedSingleTenantDataWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<RestrictedTenantDTO> localReturnType = new ParameterizedTypeReference<RestrictedTenantDTO>() {};
        return apiClient.invokeAPI("/tenant/public/single", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Gets a tenant public information [Authorization: no-auth]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param subdomain Subdomain (required)
     * @param tenantId Tenant Id (optional)
     * @return RestrictedTenantDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RestrictedTenantDTO getRestrictedTenantDataBySubdomain(String subdomain, Long tenantId) throws RestClientException {
        return getRestrictedTenantDataBySubdomainWithHttpInfo(subdomain, tenantId).getBody();
    }

    /**
     * Gets a tenant public information [Authorization: no-auth]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param subdomain Subdomain (required)
     * @param tenantId Tenant Id (optional)
     * @return ResponseEntity&lt;RestrictedTenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RestrictedTenantDTO> getRestrictedTenantDataBySubdomainWithHttpInfo(String subdomain, Long tenantId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'subdomain' is set
        if (subdomain == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'subdomain' when calling getRestrictedTenantDataBySubdomain");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("subdomain", subdomain);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "tenantId", tenantId));

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<RestrictedTenantDTO> localReturnType = new ParameterizedTypeReference<RestrictedTenantDTO>() {};
        return apiClient.invokeAPI("/tenant/public/{subdomain}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Gets a tenant public information [Authorization: no-auth]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param tenantId Tenant ID (required)
     * @return RestrictedTenantDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RestrictedTenantDTO getRestrictedTenantDataByTenantId(Long tenantId) throws RestClientException {
        return getRestrictedTenantDataByTenantIdWithHttpInfo(tenantId).getBody();
    }

    /**
     * Gets a tenant public information [Authorization: no-auth]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>404</b> - Not found
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param tenantId Tenant ID (required)
     * @return ResponseEntity&lt;RestrictedTenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RestrictedTenantDTO> getRestrictedTenantDataByTenantIdWithHttpInfo(Long tenantId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'tenantId' is set
        if (tenantId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'tenantId' when calling getRestrictedTenantDataByTenantId");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("tenantId", tenantId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<RestrictedTenantDTO> localReturnType = new ParameterizedTypeReference<RestrictedTenantDTO>() {};
        return apiClient.invokeAPI("/tenant/public/id/{tenantId}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>404</b> - Not found
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param id Tenant ID (required)
     * @return TenantDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public TenantDTO getTenantById(Long id) throws RestClientException {
        return getTenantByIdWithHttpInfo(id).getBody();
    }

    /**
     * Gets a tenant by its id [Authorization: Role: tenant-admin or single-tenant-admin]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>404</b> - Not found
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param id Tenant ID (required)
     * @return ResponseEntity&lt;TenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<TenantDTO> getTenantByIdWithHttpInfo(Long id) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling getTenantById");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TenantDTO> localReturnType = new ParameterizedTypeReference<TenantDTO>() {};
        return apiClient.invokeAPI("/tenant/{id}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Get tenants matching the given query [Auth: tenant-admin]
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid role/authorization
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param query URL-encoded infix to search for name or id. A non-encoded star symbol searches for all. (required)
     * @param page Page number (first page &#x3D; 1) (optional, default to 1)
     * @param perPage Number of items returned per page (optional, default to 10)
     * @param field field to sort by (optional, default to NAME)
     * @param order sort order (optional, default to ASC)
     * @return TenantsSearchResultDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public TenantsSearchResultDTO searchTenants(String query, Integer page, Integer perPage, String field, String order) throws RestClientException {
        return searchTenantsWithHttpInfo(query, page, perPage, field, order).getBody();
    }

    /**
     * Get tenants matching the given query [Auth: tenant-admin]
     * 
     * <p><b>200</b> - OK - successful operation
     * <p><b>400</b> - BAD REQUEST - invalid/incomplete request or body object
     * <p><b>401</b> - UNAUTHORIZED - no/invalid role/authorization
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param query URL-encoded infix to search for name or id. A non-encoded star symbol searches for all. (required)
     * @param page Page number (first page &#x3D; 1) (optional, default to 1)
     * @param perPage Number of items returned per page (optional, default to 10)
     * @param field field to sort by (optional, default to NAME)
     * @param order sort order (optional, default to ASC)
     * @return ResponseEntity&lt;TenantsSearchResultDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<TenantsSearchResultDTO> searchTenantsWithHttpInfo(String query, Integer page, Integer perPage, String field, String order) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'query' is set
        if (query == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'query' when calling searchTenants");
        }
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "query", query));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "perPage", perPage));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "field", field));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "order", order));

        final String[] localVarAccepts = { 
            "application/hal+json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TenantsSearchResultDTO> localReturnType = new ParameterizedTypeReference<TenantsSearchResultDTO>() {};
        return apiClient.invokeAPI("/tenantadmin/search", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Updates a tenant [Authorization: Role: tenant-admin or single-tenant-admin]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>409</b> - CONFLICT - unique constraint validation fails
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param id Tenant ID (required)
     * @param multilingualTenantDTO  (optional)
     * @return MultilingualTenantDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public MultilingualTenantDTO updateTenant(Long id, MultilingualTenantDTO multilingualTenantDTO) throws RestClientException {
        return updateTenantWithHttpInfo(id, multilingualTenantDTO).getBody();
    }

    /**
     * Updates a tenant [Authorization: Role: tenant-admin or single-tenant-admin]
     * 
     * <p><b>200</b> - Successful operation
     * <p><b>401</b> - UNAUTHORIZED - no/invalid Keycloak token
     * <p><b>409</b> - CONFLICT - unique constraint validation fails
     * <p><b>500</b> - INTERNAL SERVER ERROR - server encountered unexpected condition
     * @param id Tenant ID (required)
     * @param multilingualTenantDTO  (optional)
     * @return ResponseEntity&lt;MultilingualTenantDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<MultilingualTenantDTO> updateTenantWithHttpInfo(Long id, MultilingualTenantDTO multilingualTenantDTO) throws RestClientException {
        Object localVarPostBody = multilingualTenantDTO;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling updateTenant");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<MultilingualTenantDTO> localReturnType = new ParameterizedTypeReference<MultilingualTenantDTO>() {};
        return apiClient.invokeAPI("/tenantadmin/{id}", HttpMethod.PUT, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
}
