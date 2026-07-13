package de.caritas.cob.consultingtypeservice.filter;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Reads (or generates) an {@code X-Correlation-ID} request header and puts it into the SLF4J MDC
 * under key {@code CID} so it shows up in every log line for the request (see logback-spring.xml).
 * Same header/MDC-key naming as ORISO-UserService so SigNoz queries can correlate requests across
 * services.
 */
@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  private static final String HEADER_NAME = "X-Correlation-ID";
  private static final String MDC_NAME = "CID";

  @Override
  @SuppressWarnings("NullableProblems")
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    var correlationId = request.getHeader(HEADER_NAME);

    if (isEmpty(correlationId)) {
      log.debug("No correlation-id header '{}' has been found in request.", HEADER_NAME);

      correlationId = UUID.randomUUID().toString();
      log.debug("Set correlation id '{}' in response header '{}'.", correlationId, HEADER_NAME);
      response.addHeader(HEADER_NAME, correlationId);
    } else {
      log.debug("Correlation-id header '{}' with value '{}' found.", HEADER_NAME, correlationId);
    }

    MDC.put(MDC_NAME, correlationId);
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
