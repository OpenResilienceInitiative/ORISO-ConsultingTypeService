package de.caritas.cob.consultingtypeservice.api.controller;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Regression test for OBS-P2 (SigNoz OTLP tracing). Unlike TenantService (which was missing the
 * tracing autoconfiguration modules entirely - see that service's own TracingSmokeIT), this
 * service's beans, jars and config were all confirmed correct via live inspection on Pre-Dev
 * (/actuator/beans showed the full Tracer/SdkTracerProvider/OtlpTracingAutoConfiguration chain,
 * identical to AgencyService). The zero-trace symptom here traced back to the default 10% root
 * sampler probability (management.tracing.sampling.probability, unset -&gt; TracingProperties' Java
 * default of 0.10f) combined with low, ad-hoc request volume during manual verification: a handful
 * of curls can easily produce zero *sampled* traces purely by chance, which is indistinguishable
 * from "tracing is broken" by looking at ClickHouse alone. This service's application.properties
 * now pins sampling to 100% by default (env-overridable), so this test - and Pre-Dev observability
 * in general - doesn't depend on request volume or luck.
 *
 * <p>This test runs the full filter chain (Spring Security + Spring MVC's ObservationFilter)
 * against a real request and asserts that an actual OpenTelemetry span was finished and handed to
 * an exporter, via a real {@link InMemorySpanExporter} wired in alongside the app's normal (OTLP)
 * exporter through the same {@code SpanExporters} aggregation the production code uses.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(
    properties = {"spring.profiles.active=testing", "management.tracing.sampling.probability=1.0"})
@AutoConfigureMockMvc
@Import(TracingSmokeIT.InMemorySpanExporterConfig.class)
class TracingSmokeIT {

  @Autowired private WebApplicationContext context;
  @Autowired private InMemorySpanExporter inMemorySpanExporter;
  @Autowired private SdkTracerProvider sdkTracerProvider;

  private MockMvc mockMvc;

  @TestConfiguration
  static class InMemorySpanExporterConfig {

    @Bean
    InMemorySpanExporter inMemorySpanExporter() {
      return InMemorySpanExporter.create();
    }

    @Bean
    SpanExporter inMemoryTestSpanExporter(InMemorySpanExporter inMemorySpanExporter) {
      return inMemorySpanExporter;
    }
  }

  @BeforeEach
  void setup() {
    inMemorySpanExporter.reset();
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @AfterEach
  void tearDown() {
    inMemorySpanExporter.reset();
  }

  @Test
  void actuatorHealthRequest_Should_produceARecordedSpan() throws Exception {
    mockMvc
        .perform(get("/actuator/health").contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    sdkTracerProvider.forceFlush().join(5, TimeUnit.SECONDS);

    assertThat(inMemorySpanExporter.getFinishedSpanItems())
        .as("no span was exported for a real /actuator/health request")
        .isNotEmpty();
  }
}
