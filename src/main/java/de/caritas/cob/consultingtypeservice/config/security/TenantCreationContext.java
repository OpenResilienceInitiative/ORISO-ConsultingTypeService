package de.caritas.cob.consultingtypeservice.config.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class TenantCreationContext {
  public static final String HEADER = "X-ORISO-Tenant-Creation-Context";
  private final Environment environment;
  private final TaskServiceIdentity identity;

  public TenantCreationContext(Environment environment, TaskServiceIdentity identity) {
    this.environment = environment;
    this.identity = identity;
    if (java.util.Arrays.asList(
            environment.getProperty("TASK_IDENTITY_REQUIRED_TASKS", "").split(","))
        .stream()
        .map(String::trim)
        .anyMatch("CONFIG_WIZARD"::equals)) {
      try {
        byte[] key =
            TaskContextProof.grouped(environment)
                ? TaskContextProof.key(environment)
                : Base64.getDecoder()
                    .decode(environment.getProperty("ORISO_TENANT_CREATION_CONTEXT_KEY", ""));
        if (key.length < 32) {
          throw new IllegalArgumentException();
        }
      } catch (IllegalArgumentException ex) {
        throw new IllegalStateException("Required tenant creation context key is not configured");
      }
    }
  }

  private byte[] signature(String payload) throws Exception {
    byte[] key =
        Base64.getDecoder()
            .decode(environment.getProperty("ORISO_TENANT_CREATION_CONTEXT_KEY", ""));
    if (key.length < 32) {
      throw new AccessDeniedException("Tenant creation context key is not configured");
    }
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(key, "HmacSHA256"));
    return mac.doFinal(payload.getBytes(StandardCharsets.US_ASCII));
  }

  /** C16 is bound to the actual received DTO before conversion, claim or allocator access. */
  public String require(ConsultingTypeDTO dto, String proof) {
    return require(dto, proof, false);
  }

  public String requireCompensation(ConsultingTypeDTO dto, String proof) {
    if (!TaskContextProof.grouped(environment))
      throw new AccessDeniedException("Owned C bootstrap compensation required");
    return require(dto, proof, true);
  }

  private String require(ConsultingTypeDTO dto, String proof, boolean compensation) {
    if (!TaskContextProof.grouped(environment)) {
      require(dto == null || dto.getTenantId() == null ? 0L : dto.getTenantId().longValue(), proof);
      return null;
    }
    if (!identity.current("CONFIG_WIZARD")
        || dto == null
        || dto.getTenantId() == null
        || dto.getTenantId() < 1) {
      throw new AccessDeniedException("Verified tenant bootstrap required");
    }
    try {
      byte[] key = TaskContextProof.key(environment);
      JsonNode claims = TaskContextProof.verify(key, proof);
      Set<String> fields = new HashSet<>();
      claims.fieldNames().forEachRemaining(fields::add);
      if (!fields.equals(
          Set.of(
              "v",
              "iss",
              "aud",
              "actor",
              "purpose",
              "operation",
              "method",
              "azp",
              "sub",
              "tokenIssuer",
              "tenantId",
              "target",
              "payloadDigest",
              "nonce",
              "iat",
              "exp"))) {
        throw new IllegalArgumentException();
      }
      Jwt caller = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      long tenantId = dto.getTenantId().longValue();
      long issued = TaskContextProof.integer(claims.path("iat"));
      long expires = TaskContextProof.integer(claims.path("exp"));
      long now = Instant.now().getEpochSecond();
      if (TaskContextProof.integer(claims.path("v")) != 2
          || TaskContextProof.integer(claims.path("tenantId")) != tenantId
          || !text(claims, "iss", "tenantservice")
          || !text(claims, "aud", "consultingtypeservice")
          || !text(claims, "actor", "SETUP")
          || !text(claims, "purpose", "CONFIG_WIZARD")
          || !text(claims, "operation", compensation ? "tenant.consulting-type.compensate" : "tenant.consulting-type.bootstrap")
          || !text(claims, "method", "POST")
          || !text(claims, "target", "tenant/" + tenantId + (compensation ? "/consulting-type/default/compensation" : "/consulting-type/default"))
          || !text(claims, "azp", caller.getClaimAsString("azp"))
          || !text(claims, "sub", caller.getSubject())
          || caller.getIssuer() == null
          || !text(claims, "tokenIssuer", caller.getIssuer().toString())
          || issued < 0
          || issued > now
          || expires <= now
          || expires <= issued
          || expires - issued > 60
          || !claims.path("nonce").isTextual()
          || !claims.path("nonce").textValue().matches("[a-f0-9]{32}")
          || !claims.path("payloadDigest").isTextual()
          || !claims.path("payloadDigest").textValue().matches("[a-f0-9]{64}")
          || !MessageDigest.isEqual(
              TaskContextProof.digest(key, dto).getBytes(StandardCharsets.US_ASCII),
              claims.path("payloadDigest").textValue().getBytes(StandardCharsets.US_ASCII))) {
        throw new IllegalArgumentException();
      }
      return claims.path("nonce").textValue();
    } catch (Exception invalid) {
      throw new AccessDeniedException("Invalid tenant bootstrap context");
    }
  }

  private static boolean text(JsonNode claims, String field, String expected) {
    return expected != null
        && claims.path(field).isTextual()
        && expected.equals(claims.path(field).textValue());
  }

  public void require(long tenantId, String proof) {
    if (TaskContextProof.grouped(environment)
        || !identity.current("CONFIG_WIZARD")
        || tenantId < 1
        || proof == null
        || proof.length() > 2048) {
      throw new AccessDeniedException("Verified tenant bootstrap required");
    }
    try {
      String[] parts = proof.split("\\.", -1);
      if (parts.length != 2
          || !MessageDigest.isEqual(signature(parts[0]), Base64.getUrlDecoder().decode(parts[1]))) {
        throw new IllegalArgumentException();
      }
      Map<String, Object> claims =
          new ObjectMapper()
              .readValue(
                  Base64.getUrlDecoder().decode(parts[0]),
                  new TypeReference<Map<String, Object>>() {});
      Jwt caller = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      long now = Instant.now().getEpochSecond();
      long issued = ((Number) claims.get("iat")).longValue();
      long expires = ((Number) claims.get("exp")).longValue();
      if (claims.size() != 10
          || !"tenantservice".equals(claims.get("iss"))
          || !"consultingtypeservice".equals(claims.get("aud"))
          || ((Number) claims.get("v")).intValue() != 1
          || ((Number) claims.get("tenantId")).longValue() != tenantId
          || !caller.getSubject().equals(claims.get("sub"))
          || !caller.getClaimAsString("azp").equals(claims.get("azp"))
          || caller.getIssuer() == null
          || !caller.getIssuer().toString().equals(claims.get("tokenIssuer"))
          || issued > now + 5
          || expires <= now - 5
          || expires <= issued
          || expires - issued > 60
          || !(claims.get("nonce") instanceof String)
          || ((String) claims.get("nonce")).isBlank()) {
        throw new IllegalArgumentException();
      }
    } catch (Exception ex) {
      throw new AccessDeniedException("Invalid tenant bootstrap context");
    }
  }
}
