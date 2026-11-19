package de.caritas.cob.consultingtypeservice.config.resttemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

class RestTemplateConfigTest {

  @Test
  void suppliesConnectionAndResponseLimitsAndKeepsTheErrorHandler() {
    var settings = new AtomicReference<HttpClientSettings>();
    var builder =
        new RestTemplateBuilder()
            .requestFactoryBuilder(
                configuredSettings -> {
                  settings.set(configuredSettings);
                  return new SimpleClientHttpRequestFactory();
                });

    var client = new RestTemplateConfig().restTemplate(builder);

    assertThat(settings.get().connectTimeout()).isEqualTo(Duration.ofSeconds(3));
    assertThat(settings.get().readTimeout()).isEqualTo(Duration.ofSeconds(8));
    assertThat(client.getErrorHandler()).isInstanceOf(CustomResponseErrorHandler.class);
  }
}
