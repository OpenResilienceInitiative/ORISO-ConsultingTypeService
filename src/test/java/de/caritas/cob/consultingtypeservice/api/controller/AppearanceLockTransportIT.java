package de.caritas.cob.consultingtypeservice.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepository;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypePatchDTO;
import de.caritas.cob.consultingtypeservice.config.apiclient.TenantServiceApiControllerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * The editing lock over a real HTTP connection (ORISO-ConsultingTypeService#190). Nothing of the
 * TenantService client is mocked: a local HTTP server stands in for TenantService, so the request
 * path, the {@code Authorization} header that is really sent, the deserialization of the answer,
 * the timeouts and an unset URL are exercised as in operation.
 *
 * <p>The answer is shaped after the provider's schema {@code TenantPermissionPolicies} (ORISO-
 * TenantService {@code api/tenantservice.yaml}, {@code GET /tenantadmin/{id}/permission-policies}),
 * including fields this service does not know. It was not recorded from a running TenantService.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@ActiveProfiles("testing")
@AutoConfigureTestDatabase
@TestPropertySource(properties = "tenant.service.lock.read-timeout-ms=700")
class AppearanceLockTransportIT {

  private static final int TENANT = 7901;
  private static final int TYPE = 79011;

  private static final String LOCKED_ANSWER =
      "{\"tenantId\":7901,\"policies\":{"
          + "\"appearance\":{\"value\":false,\"mode\":\"ENFORCED\",\"inherited\":true},"
          + "\"calls\":{\"value\":true,\"mode\":\"SUGGESTED\",\"inherited\":false}},"
          + "\"caseHandoverPolicies\":{\"reasons\":{}},"
          + "\"fieldAddedLater\":{\"anything\":1}}";
  private static final String UNLOCKED_ANSWER =
      LOCKED_ANSWER.replace("\"value\":false", "\"value\":true");

  /** What the stand-in TenantService does with the next request. */
  private record Answer(int status, String body, long delayMs) {}

  private record Seen(String method, String path, String authorization) {}

  private static final HttpServer TENANT_SERVICE = startTenantService();
  private static final AtomicReference<Answer> ANSWER = new AtomicReference<>();
  private static final List<Seen> SEEN = new CopyOnWriteArrayList<>();

  private static HttpServer startTenantService() {
    try {
      HttpServer server =
          HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
      server.setExecutor(Executors.newCachedThreadPool());
      server.createContext(
          "/",
          exchange -> {
            SEEN.add(
                new Seen(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI().getPath(),
                    exchange.getRequestHeaders().getFirst("Authorization")));
            Answer answer = ANSWER.get();
            try {
              Thread.sleep(answer.delayMs());
              byte[] body = answer.body().getBytes(StandardCharsets.UTF_8);
              exchange.getResponseHeaders().add("Content-Type", "application/json");
              exchange.sendResponseHeaders(answer.status(), body.length);
              exchange.getResponseBody().write(body);
            } catch (Exception callerGaveUp) {
              // the stalled case: the client has closed the connection
            } finally {
              exchange.close();
            }
          });
      server.start();
      return server;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @DynamicPropertySource
  static void tenantServiceUrl(DynamicPropertyRegistry registry) {
    registry.add(
        "tenant.service.api.url",
        () -> "http://localhost:" + TENANT_SERVICE.getAddress().getPort());
  }

  @AfterAll
  static void stopTenantService() {
    TENANT_SERVICE.stop(0);
  }

  private static final EasyRandom EASY_RANDOM = new EasyRandom();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Autowired private WebApplicationContext context;
  @Autowired private ConsultingTypeRepository consultingTypeRepository;
  @Autowired private ConsultingTypeConverter consultingTypeConverter;
  @Autowired private TenantServiceApiControllerFactory tenantServiceFactory;

  @MockitoBean private JwtDecoder jwtDecoder;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    SEEN.clear();
    removeType();
    ConsultingTypeDTO dto = EASY_RANDOM.nextObject(ConsultingTypeDTO.class).tenantId(TENANT);
    dto.slug("transport-type");
    dto.getRoles().getConsultant().addRoleNames("test", Arrays.asList("test"));
    var converted = consultingTypeConverter.convert(dto);
    converted.setId(TYPE);
    converted.setLanguageFormal(true);
    converted.setIsVideoCallAllowed(false);
    var entity = new ConsultingTypeEntity();
    BeanUtils.copyProperties(converted, entity);
    consultingTypeRepository.insert(entity);
    when(jwtDecoder.decode(any(String.class)))
        .thenReturn(
            Jwt.withTokenValue("caller-token")
                .header("alg", "none")
                .claim("sub", "subject")
                .claim("tenantId", String.valueOf(TENANT))
                .claim("realm_access", Map.of("roles", List.of("tenant-admin")))
                .build());
  }

  @AfterEach
  void removeType() {
    consultingTypeRepository.findAll().stream()
        .filter(type -> type.getId() == TYPE)
        .forEach(consultingTypeRepository::delete);
  }

  private ResultActions changeFormOfAddress() throws Exception {
    return mockMvc.perform(
        patch("/consultingtypes/" + TYPE)
            .header("Authorization", "Bearer caller-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                OBJECT_MAPPER.writeValueAsString(
                    new ConsultingTypePatchDTO().languageFormal(false).isVideoCallAllowed(false))));
  }

  private Boolean storedFormOfAddress() {
    return consultingTypeRepository.findByConsultingTypeId(TYPE).getLanguageFormal();
  }

  @Test
  void patch_Should_beRefused_When_theRealAnswerSaysLocked_AndSendTheCallersTokenToTheRightPath()
      throws Exception {
    ANSWER.set(new Answer(200, LOCKED_ANSWER, 0));

    changeFormOfAddress().andExpect(status().isForbidden());

    assertThat(SEEN)
        .containsExactly(
            new Seen(
                "GET", "/tenantadmin/" + TENANT + "/permission-policies", "Bearer caller-token"));
    assertThat(storedFormOfAddress()).isTrue();
  }

  @Test
  void patch_Should_succeed_When_theRealAnswerSaysUnlocked() throws Exception {
    ANSWER.set(new Answer(200, UNLOCKED_ANSWER, 0));

    changeFormOfAddress().andExpect(status().isOk());

    assertThat(storedFormOfAddress()).isFalse();
  }

  @Test
  void patch_Should_answerUnavailable_When_tenantServiceAnswersWithAnError() throws Exception {
    ANSWER.set(new Answer(500, "{}", 0));

    changeFormOfAddress().andExpect(status().isServiceUnavailable());

    assertThat(storedFormOfAddress()).isTrue();
  }

  @Test
  void patch_Should_answerUnavailable_When_tenantServiceAnswersWithGarbage() throws Exception {
    ANSWER.set(new Answer(200, "<html>not json</html>", 0));

    changeFormOfAddress().andExpect(status().isServiceUnavailable());

    assertThat(storedFormOfAddress()).isTrue();
  }

  @Test
  void patch_Should_answerUnavailableInTime_When_tenantServiceStalls() throws Exception {
    ANSWER.set(new Answer(200, LOCKED_ANSWER, 4000));

    long started = System.nanoTime();
    changeFormOfAddress().andExpect(status().isServiceUnavailable());
    long tookMs = (System.nanoTime() - started) / 1_000_000;

    // read timeout is 700 ms here; the stand-in would have answered after 4 s
    assertThat(tookMs).isLessThan(3000);
    assertThat(storedFormOfAddress()).isTrue();
  }

  @Test
  void patch_Should_answerUnavailable_When_theTenantServiceUrlIsEmpty() throws Exception {
    Object configured = ReflectionTestUtils.getField(tenantServiceFactory, "tenantServiceApiUrl");
    ReflectionTestUtils.setField(tenantServiceFactory, "tenantServiceApiUrl", "");
    try {
      changeFormOfAddress().andExpect(status().isServiceUnavailable());
    } finally {
      ReflectionTestUtils.setField(tenantServiceFactory, "tenantServiceApiUrl", configured);
    }

    assertThat(SEEN).isEmpty();
    assertThat(storedFormOfAddress()).isTrue();
  }
}
