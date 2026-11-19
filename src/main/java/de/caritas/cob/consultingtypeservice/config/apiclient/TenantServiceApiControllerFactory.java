package de.caritas.cob.consultingtypeservice.config.apiclient;

import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TenantServiceApiControllerFactory {

  @Value("${tenant.service.api.url}")
  private String tenantServiceApiUrl;

  @Autowired private RestTemplate restTemplate;

  public TenantControllerApi createControllerApi() {
    return createControllerApi(restTemplate);
  }

  /** For a caller that needs its own timeouts instead of the shared template's. */
  public TenantControllerApi createControllerApi(RestTemplate callersRestTemplate) {
    var apiClient =
        new TenantServiceApiClient(callersRestTemplate).setBasePath(this.tenantServiceApiUrl);
    return new TenantControllerApi(apiClient);
  }
}
