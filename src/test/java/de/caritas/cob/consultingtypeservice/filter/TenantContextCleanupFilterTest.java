package de.caritas.cob.consultingtypeservice.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.config.CorsConfig;
import de.caritas.cob.consultingtypeservice.config.TrailingSlashFilter;
import jakarta.servlet.ServletException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

class TenantContextCleanupFilterTest {
  private final TenantContextCleanupFilter filter = new TenantContextCleanupFilter();

  @AfterEach
  void clearTenant() {
    TenantContext.clear();
  }

  @Test
  void runsBeforeTheOtherEarlyServletFilters() {
    var cors = new CorsConfig();
    ReflectionTestUtils.setField(cors, "allowedOrigins", List.of("https://test-tenant.internal"));
    ReflectionTestUtils.setField(cors, "allowedPaths", new String[] {"/**"});
    var cleanupOrder = OrderUtils.getOrder(TenantContextCleanupFilter.class);

    assertThat(cleanupOrder)
        .isLessThan(cors.corsFilterRegistrationBean().getOrder())
        .isLessThan(OrderUtils.getOrder(TrailingSlashFilter.class));
  }

  @Test
  void anEarlyResponseKeepsItsStatusAndLeavesAnEmptyThread() throws Exception {
    TenantContext.setCurrentTenant(17L);
    var response = new MockHttpServletResponse();

    filter.doFilter(
        new MockHttpServletRequest(),
        response,
        (request, ignored) -> {
          assertThat(TenantContext.getCurrentTenant()).isNull();
          TenantContext.setCurrentTenant(42L);
          response.setStatus(403);
        });

    assertThat(response.getStatus()).isEqualTo(403);
    assertThat(TenantContext.getCurrentTenant()).isNull();
    filter.doFilter(
        new MockHttpServletRequest(),
        new MockHttpServletResponse(),
        (request, nextResponse) -> assertThat(TenantContext.getCurrentTenant()).isNull());
  }

  @Test
  void aDownstreamFailurePreservesTheErrorAndClearsTheContext() {
    var failure = new ServletException("synthetic filter failure");
    assertThatThrownBy(
            () ->
                filter.doFilter(
                    new MockHttpServletRequest(),
                    new MockHttpServletResponse(),
                    (request, response) -> {
                      TenantContext.setCurrentTenant(42L);
                      throw failure;
                    }))
        .isSameAs(failure);
    assertThat(TenantContext.getCurrentTenant()).isNull();
  }
}
