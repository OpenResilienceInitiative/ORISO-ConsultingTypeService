package de.caritas.cob.consultingtypeservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Spring Boot 4 / Spring Framework 7 removed trailing-slash path matching ({@code
 * setUseTrailingSlashMatch}). Some existing clients (e.g. the registration frontend calling {@code
 * /topic/public/}) still send a trailing slash, which now returns 404 ("No static resource").
 *
 * <p>This filter transparently strips a trailing slash from the request path so those requests
 * match the controller mappings again, restoring the pre-SB4 behaviour without editing generated
 * API interfaces. Kept intentionally narrow: only rewrites the perceived path, never redirects.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrailingSlashFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String uri = request.getRequestURI();
    if (uri != null && uri.length() > 1 && uri.endsWith("/")) {
      final String trimmedUri = uri.replaceAll("/+$", "");
      final String servletPath = request.getServletPath();
      final String trimmedServletPath =
          servletPath == null ? null : servletPath.replaceAll("/+$", "");
      final HttpServletRequestWrapper wrapped =
          new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
              return trimmedUri.isEmpty() ? "/" : trimmedUri;
            }

            @Override
            public String getServletPath() {
              return trimmedServletPath;
            }
          };
      filterChain.doFilter(wrapped, response);
      return;
    }
    filterChain.doFilter(request, response);
  }
}
