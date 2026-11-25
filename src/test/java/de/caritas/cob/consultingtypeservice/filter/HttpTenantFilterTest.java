package de.caritas.cob.consultingtypeservice.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpTenantFilterTest {

  @InjectMocks HttpTenantFilter httpTenantFilter;

  @Mock private TenantResolver tenantResolver;

  @Mock HttpServletRequest request;

  @Mock HttpServletResponse response;

  @Mock FilterChain filterChain;

  @AfterEach
  void clearTenant() {
    TenantContext.clear();
  }

  @Test
  void whitelistedRequestStartsEmptyAndClearsContextCreatedByTheHandler() throws Exception {
    TenantContext.setCurrentTenant(17L);
    Mockito.when(request.getRequestURI()).thenReturn("/actuator/health");
    Mockito.doAnswer(
            invocation -> {
              assertThat(TenantContext.getCurrentTenant()).isNull();
              TenantContext.setCurrentTenant(42L);
              return null;
            })
        .when(filterChain)
        .doFilter(request, response);

    httpTenantFilter.doFilterInternal(request, response, filterChain);

    assertThat(TenantContext.getCurrentTenant()).isNull();
    Mockito.verifyNoInteractions(tenantResolver);
  }

  @Test
  void failedResolutionClearsContextBeforeAnyDownstreamHandler() {
    Mockito.when(request.getRequestURI()).thenReturn("/topic-groups");
    var failure = new IllegalStateException("synthetic resolver failure");
    Mockito.when(tenantResolver.resolve())
        .thenAnswer(
            invocation -> {
              TenantContext.setCurrentTenant(42L);
              throw failure;
            });

    assertThatThrownBy(() -> httpTenantFilter.doFilterInternal(request, response, filterChain))
        .isSameAs(failure);
    assertThat(TenantContext.getCurrentTenant()).isNull();
    Mockito.verifyNoInteractions(filterChain);
  }

  @Test
  void doFilterInternal_Should_NotApply_When_RequestBelongsToTenancyWhiteList()
      throws ServletException, IOException {
    // given
    Mockito.when(request.getRequestURI()).thenReturn("/actuator/health/liveness");

    // when
    httpTenantFilter.doFilterInternal(request, response, filterChain);

    // then
    Mockito.verifyNoInteractions(tenantResolver);
  }

  @Test
  void doFilterInternal_Should_Apply_When_DoesNotBelongBelongsToTenancyWhiteList()
      throws ServletException, IOException {

    // given
    Mockito.when(request.getRequestURI()).thenReturn("/consultingtypes/1");

    // when
    httpTenantFilter.doFilterInternal(request, response, filterChain);

    // then
    Mockito.verify(tenantResolver).resolve();
  }

  @Test
  void doFilterInternal_Should_ClearTheTenant_When_TheChainThrows()
      throws ServletException, IOException {
    Mockito.when(request.getRequestURI()).thenReturn("/topic-groups");
    Mockito.when(tenantResolver.resolve()).thenReturn(7L);
    Mockito.doThrow(new ServletException("boom")).when(filterChain).doFilter(request, response);

    try {
      assertThatThrownBy(() -> httpTenantFilter.doFilterInternal(request, response, filterChain))
          .isInstanceOf(ServletException.class);

      assertThat(TenantContext.getCurrentTenant()).isNull();
    } finally {
      TenantContext.clear();
    }
  }
}
