package de.caritas.cob.consultingtypeservice.config.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
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
        if (Base64.getDecoder()
                .decode(environment.getProperty("ORISO_TENANT_CREATION_CONTEXT_KEY", ""))
                .length
            < 32) {
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

  public void require(long tenantId, String proof) {
    if (!identity.current("CONFIG_WIZARD")
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
