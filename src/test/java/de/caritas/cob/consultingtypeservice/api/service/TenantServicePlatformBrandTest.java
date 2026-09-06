package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.config.apiclient.TenantServiceApiControllerFactory;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class TenantServicePlatformBrandTest {
  @Test
  void platformLookupExplicitlySuppressesTargetTenantOverride() {
    var factory = mock(TenantServiceApiControllerFactory.class);
    var api = mock(TenantControllerApi.class);
    var platform = new RestrictedTenantDTO();
    platform.setId(7L);
    when(factory.createControllerApi()).thenReturn(api);
    when(api.getRestrictedTenantDataBySubdomainWithHttpInfo("platform", 0L))
        .thenReturn(ResponseEntity.ok(platform));
    TenantContext.setCurrentTenant(40L);
    try {
      assertThat(new TenantService(factory).getPlatformTenantData("platform")).isSameAs(platform);
      verify(api).getRestrictedTenantDataBySubdomainWithHttpInfo("platform", 0L);
    } finally {
      TenantContext.clear();
    }
  }
}
