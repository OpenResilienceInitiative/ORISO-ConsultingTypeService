package de.caritas.cob.consultingtypeservice.filter;

import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Clears request-scoped tenant state, including exits before tenant resolution. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextCleanupFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    TenantContext.clear();
    try {
      chain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }
}
