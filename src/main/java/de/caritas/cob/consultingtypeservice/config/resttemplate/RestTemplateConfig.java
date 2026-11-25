package de.caritas.cob.consultingtypeservice.config.resttemplate;

import java.time.Duration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** Contains the rest template configuration. */
@Configuration
public class RestTemplateConfig {

  /** Maximum time to establish a connection to TenantService. */
  private static final int CONNECT_TIMEOUT_SECONDS = 3;

  /** Maximum time to wait for a response from TenantService. */
  private static final int READ_TIMEOUT_SECONDS = 8;

  /**
   * RestTemplate Bean.
   *
   * @param builder {@link RestTemplateBuilder}
   * @return {@link RestTemplate}
   */
  @Bean
  public RestTemplate restTemplate(final RestTemplateBuilder builder) {
    return builder
        .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
        .readTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS))
        .errorHandler(new CustomResponseErrorHandler())
        .build();
  }
}
