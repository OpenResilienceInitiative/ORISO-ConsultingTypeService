package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * Guards the direction the service <em>consumes</em> rather than the direction it <em>accepts</em>.
 *
 * <p>TenantService owns its own response contract. Adding a field to a response is backward
 * compatible by every contract rule we apply, and {@code oasdiff} will not flag it. Our vendored
 * copy of the tenant contract under {@code services/tenantservice.yaml} therefore lags behind by
 * design and is refreshed on our own schedule.
 *
 * <p>That only stays true while deserialization of upstream responses tolerates fields we do not
 * know yet. If it ever stops tolerating them, a purely additive change in another service turns
 * into a runtime failure here, with no signal at build time in either repository.
 *
 * <p>The payload below is not invented: {@code theming.accent}, {@code theming.signal}, {@code
 * theming.loginEffect}, {@code settings.emailRequired}, {@code settings.emailVisible}, {@code
 * settings.featureAskerEmailEnabled}, {@code settings.featureDisplayNameEditable} and {@code
 * settings.smtp.passwordSet} are all present in TenantService's current published spec and absent
 * from our vendored copy.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
class TenantServiceResponseToleranceIT {

  /**
   * A tenant response carrying every field TenantService's current spec declares and our vendored
   * copy does not.
   */
  private static final String RESPONSE_WITH_FIELDS_UNKNOWN_TO_OUR_VENDORED_CONTRACT =
      "{"
          + "  \"id\": 1,"
          + "  \"name\": \"tenant\","
          + "  \"subdomain\": \"tenant\","
          + "  \"theming\": {"
          + "    \"logo\": \"logo\","
          + "    \"primaryColor\": \"#281715\","
          + "    \"accent\": \"#ffcc00\","
          + "    \"signal\": \"#cc0000\","
          + "    \"loginEffect\": \"NONE\""
          + "  },"
          + "  \"settings\": {"
          + "    \"featureStatisticsEnabled\": true,"
          + "    \"emailRequired\": true,"
          + "    \"emailVisible\": false,"
          + "    \"featureAskerEmailEnabled\": true,"
          + "    \"featureDisplayNameEditable\": false,"
          + "    \"smtp\": {"
          + "      \"host\": \"smtp.example.org\","
          + "      \"passwordSet\": true"
          + "    }"
          + "  }"
          + "}";

  @Autowired private RestTemplate restTemplate;

  @Autowired private TenantService tenantService;

  private MockRestServiceServer tenantServiceApi;

  @BeforeEach
  void bindTenantServiceApi() {
    tenantServiceApi = MockRestServiceServer.bindTo(restTemplate).build();
  }

  @Test
  void
      getRestrictedTenantDataBySubdomain_Should_IgnoreFieldsNotInOurVendoredContract_When_TenantServiceAddsResponseFields() {
    tenantServiceApi
        .expect(requestTo("http://localhost:8084/tenant/public/tenant"))
        .andRespond(
            withSuccess(
                RESPONSE_WITH_FIELDS_UNKNOWN_TO_OUR_VENDORED_CONTRACT, MediaType.APPLICATION_JSON));

    RestrictedTenantDTO tenant = tenantService.getRestrictedTenantDataBySubdomainNoCache("tenant");

    assertThat(tenant).isNotNull();
    assertThat(tenant.getSubdomain()).isEqualTo("tenant");
    assertThat(tenant.getTheming().getPrimaryColor()).isEqualTo("#281715");
    assertThat(tenant.getSettings().getFeatureStatisticsEnabled()).isTrue();
    tenantServiceApi.verify();
  }

  @Test
  void
      getRestrictedTenantDataByTenantId_Should_IgnoreFieldsNotInOurVendoredContract_When_TenantServiceAddsResponseFields() {
    tenantServiceApi
        .expect(requestTo("http://localhost:8084/tenant/public/id/1"))
        .andRespond(
            withSuccess(
                RESPONSE_WITH_FIELDS_UNKNOWN_TO_OUR_VENDORED_CONTRACT, MediaType.APPLICATION_JSON));

    RestrictedTenantDTO tenant = tenantService.getRestrictedTenantDataNoCache(1L);

    assertThat(tenant).isNotNull();
    assertThat(tenant.getId()).isEqualTo(1L);
    tenantServiceApi.verify();
  }
}
