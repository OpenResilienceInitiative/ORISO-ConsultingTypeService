package de.caritas.cob.consultingtypeservice.config.security;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeIdAllocator;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim;
import de.caritas.cob.consultingtypeservice.api.service.TenantBootstrapService;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/** Independent wire builder reaches the production guard before any Mongo operation. */
class TenantBootstrapV2ContextTest {
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final byte[] KEY = new byte[32];
  private static final String ISSUER = "https://synthetic.identity/realms/context";

  @AfterEach
  void clearIdentity() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void exactMaterializedDtoPassesTheC16GuardBeforeTheFirstMongoQuery() throws Exception {
    var mongo = mock(MongoTemplate.class);
    var converter = mock(ConsultingTypeConverter.class);
    var ids = mock(ConsultingTypeIdAllocator.class);
    var dto = new ConsultingTypeDTO();
    dto.setTenantId(42);
    when(converter.convert(dto)).thenReturn(new ConsultingType());
    var reachedMongo = new IllegalStateException("finite first-query observation");
    when(mongo.findById(eq("42"), eq(TenantBootstrapClaim.class))).thenThrow(reachedMongo);
    var service = new TenantBootstrapService(mongo, converter, receiver(), ids);
    String proof = proof(dto);
    assertSame(reachedMongo, assertThrows(IllegalStateException.class, () -> service.create(dto, proof)));
    verifyNoInteractions(ids);
  }

  @Test
  void independentlySignedProofForAnotherMaterializedDtoDeniesBeforeConverterOrMongo()
      throws Exception {
    var mongo = mock(MongoTemplate.class);
    var converter = mock(ConsultingTypeConverter.class);
    var ids = mock(ConsultingTypeIdAllocator.class);
    var signed = new ConsultingTypeDTO();
    signed.setTenantId(42);
    String proof = proof(signed);
    var received = new ConsultingTypeDTO();
    received.setTenantId(43);
    var service = new TenantBootstrapService(mongo, converter, receiver(), ids);
    assertThrows(AccessDeniedException.class, () -> service.create(received, proof));
    verifyNoInteractions(mongo, converter, ids);
  }

  private static TenantCreationContext receiver() {
    var environment = new MockEnvironment()
        .withProperty("TASK_IDENTITY_MODEL", "FIVE_ACTORS")
        .withProperty("TASK_IDENTITY_REQUIRED_TASKS", "CONFIG_WIZARD")
        .withProperty("TASK_IDENTITY_AUDIENCE", "consultingtypeservice")
        .withProperty("IDENTITY_SETUP_CLIENT_ID", "backend-setup")
        .withProperty("IDENTITY_SETUP_SERVICE_SUBJECT", "setup-subject")
        .withProperty("ORISO_TASK_CONTEXT_KEY", Base64.getEncoder().encodeToString(KEY));
    var jwt = Jwt.withTokenValue("synthetic-no-wire-token")
        .header("alg", "RS256").issuer(ISSUER).subject("setup-subject")
        .claim("azp", "backend-setup").audience(List.of("consultingtypeservice"))
        .expiresAt(Instant.now().plusSeconds(120))
        .claim("realm_access", Map.of("roles", List.of("config-wizard"))).build();
    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
    return new TenantCreationContext(environment, new TaskServiceIdentity(environment));
  }

  private static String proof(ConsultingTypeDTO dto) throws Exception {
    long now = Instant.now().getEpochSecond();
    Map<String, Object> claims = new TreeMap<>();
    claims.put("v", 2);
    claims.put("iss", "tenantservice");
    claims.put("aud", "consultingtypeservice");
    claims.put("actor", "SETUP");
    claims.put("purpose", "CONFIG_WIZARD");
    claims.put("operation", "tenant.consulting-type.bootstrap");
    claims.put("method", "POST");
    claims.put("azp", "backend-setup");
    claims.put("sub", "setup-subject");
    claims.put("tokenIssuer", ISSUER);
    claims.put("tenantId", 42);
    claims.put("target", "tenant/42/consulting-type/default");
    claims.put("payloadDigest", HexFormat.of().formatHex(mac("payload\n", canonical(JSON.valueToTree(dto)))));
    claims.put("nonce", "0123456789abcdef0123456789abcdef");
    claims.put("iat", now);
    claims.put("exp", now + 60);
    String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(canonical(JSON.valueToTree(claims)));
    return encoded + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(mac("proof\n", encoded.getBytes(StandardCharsets.US_ASCII)));
  }

  private static byte[] canonical(JsonNode value) throws Exception {
    if (value.isObject()) {
      var names = new ArrayList<String>();
      value.fieldNames().forEachRemaining(names::add);
      names.sort(String::compareTo);
      var fields = new ArrayList<String>();
      for (String name : names) {
        if (!value.get(name).isNull()) fields.add(JSON.writeValueAsString(name) + ":" + new String(canonical(value.get(name)), StandardCharsets.UTF_8));
      }
      return ("{" + String.join(",", fields) + "}").getBytes(StandardCharsets.UTF_8);
    }
    if (value.isArray()) {
      var items = new ArrayList<String>();
      for (JsonNode item : value) items.add(new String(canonical(item), StandardCharsets.UTF_8));
      return ("[" + String.join(",", items) + "]").getBytes(StandardCharsets.UTF_8);
    }
    return (value.isNumber() ? value.decimalValue().stripTrailingZeros().toPlainString() : JSON.writeValueAsString(value)).getBytes(StandardCharsets.UTF_8);
  }

  private static byte[] mac(String domain, byte[] payload) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(KEY, "HmacSHA256"));
    mac.update(domain.getBytes(StandardCharsets.US_ASCII));
    return mac.doFinal(payload);
  }
}
