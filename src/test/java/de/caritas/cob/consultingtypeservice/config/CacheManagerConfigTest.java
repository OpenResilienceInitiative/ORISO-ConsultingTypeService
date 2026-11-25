package de.caritas.cob.consultingtypeservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.config.apiclient.TenantServiceApiControllerFactory;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({CacheManagerConfig.class, TenantService.class})
@TestPropertySource(
    properties = {
      "cache.tenant.configuration.maxEntriesLocalHeap=100",
      "cache.tenant.configuration.eternal=false",
      "cache.tenant.configuration.timeToIdleSeconds=10",
      "cache.tenant.configuration.timeToLiveSeconds=60",
      "cache.groups.configuration.maxEntriesLocalHeap=0",
      "cache.groups.configuration.eternal=true",
      "cache.groups.configuration.timeToIdleSeconds=300",
      "cache.groups.configuration.timeToLiveSeconds=600"
    })
class CacheManagerConfigTest {

  @Autowired private CacheManager cacheManager;
  @Autowired private TenantService tenantService;
  @MockitoBean private TenantServiceApiControllerFactory tenantApiFactory;

  @Test
  void tenantCacheUsesSeparateKeysAndDirectLookupsBypassIt() {
    var api = mock(TenantControllerApi.class);
    when(tenantApiFactory.createControllerApi()).thenReturn(api);
    var bySubdomain = new RestrictedTenantDTO().id(1L);
    var byId = new RestrictedTenantDTO().id(2L);
    var current = new RestrictedTenantDTO().id(3L);
    when(api.getRestrictedTenantDataBySubdomainWithHttpInfo("1", null))
        .thenReturn(ResponseEntity.ok(bySubdomain), ResponseEntity.ok(current));
    when(api.getRestrictedTenantDataByTenantId(1L)).thenReturn(byId);

    assertThat(tenantService.getRestrictedTenantDataBySubdomain("1")).isSameAs(bySubdomain);
    assertThat(tenantService.getRestrictedTenantData(1L)).isSameAs(byId);
    assertThat(tenantService.getRestrictedTenantDataBySubdomain("1")).isSameAs(bySubdomain);
    assertThat(tenantService.getRestrictedTenantData(1L)).isSameAs(byId);
    assertThat(tenantService.getRestrictedTenantDataBySubdomainNoCache("1")).isSameAs(current);
    assertThat(tenantService.getRestrictedTenantDataBySubdomain("1")).isSameAs(bySubdomain);

    verify(api, times(2)).getRestrictedTenantDataBySubdomainWithHttpInfo("1", null);
    verify(api).getRestrictedTenantDataByTenantId(1L);
  }

  @Test
  void onlyTheTwoDeclaredCachesExistAndTheGroupCacheStillAllowsNulls() {
    assertThat(cacheManager.getCacheNames())
        .containsExactlyInAnyOrder(
            CacheManagerConfig.TENANT_CACHE, CacheManagerConfig.CONSULTING_TYPE_GROUPS_CACHE);
    assertThat(cacheManager.getCache("unknown")).isNull();
    var groups = cacheManager.getCache(CacheManagerConfig.CONSULTING_TYPE_GROUPS_CACHE);
    assertThat(groups).isNotNull();
    groups.put("absent", null);
    assertThat(groups.get("absent")).isNotNull();
    assertThat(groups.get("absent").get()).isNull();
  }

  @Test
  void preservesSizeIdleLifetimeAndEternalPolicies() {
    var tenant = (CaffeineCache) cacheManager.getCache(CacheManagerConfig.TENANT_CACHE);
    var tenantPolicy = tenant.getNativeCache().policy();
    assertThat(tenantPolicy.eviction().orElseThrow().getMaximum()).isEqualTo(100);
    assertThat(tenantPolicy.expireAfterAccess().orElseThrow().getExpiresAfter())
        .isEqualTo(Duration.ofSeconds(10));
    assertThat(tenantPolicy.expireAfterWrite().orElseThrow().getExpiresAfter())
        .isEqualTo(Duration.ofSeconds(60));

    var groups =
        (CaffeineCache) cacheManager.getCache(CacheManagerConfig.CONSULTING_TYPE_GROUPS_CACHE);
    var groupPolicy = groups.getNativeCache().policy();
    assertThat(groupPolicy.eviction()).isEmpty();
    assertThat(groupPolicy.expireAfterAccess()).isEmpty();
    assertThat(groupPolicy.expireAfterWrite()).isEmpty();
  }
}
