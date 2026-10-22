package de.caritas.cob.consultingtypeservice.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

/** Immutable bootstrap attempt with a reserved result and exact actor/payload provenance. */
@Document("tenant_bootstrap_claims")
public final class TenantBootstrapClaim {
  @Id private final String claimId;
  private final String tenantId;
  private final String attempt;
  private final Integer resultId;
  private final String payloadDigest;
  private final String actorClient;
  private final String actorSubject;
  private final String actorIssuer;

  public TenantBootstrapClaim(
      String tenantId,
      String attempt,
      Integer resultId,
      String payloadDigest,
      String actorClient,
      String actorSubject,
      String actorIssuer) {
    this(tenantId, tenantId, attempt, resultId, payloadDigest, actorClient, actorSubject, actorIssuer);
  }

  @PersistenceCreator
  public TenantBootstrapClaim(
      String claimId,
      String tenantId,
      String attempt,
      Integer resultId,
      String payloadDigest,
      String actorClient,
      String actorSubject,
      String actorIssuer) {
    this.claimId = claimId;
    // Existing v1 documents stored the tenant directly as their id without a second scalar.
    this.tenantId = tenantId == null ? claimId : tenantId;
    this.attempt = attempt;
    this.resultId = resultId;
    this.payloadDigest = payloadDigest;
    this.actorClient = actorClient;
    this.actorSubject = actorSubject;
    this.actorIssuer = actorIssuer;
  }

  public String claimId() {
    return claimId;
  }

  public String tenantId() {
    return tenantId;
  }

  public String attempt() {
    return attempt;
  }

  public Integer resultId() {
    return resultId;
  }

  public String payloadDigest() {
    return payloadDigest;
  }

  public String actorClient() {
    return actorClient;
  }

  public String actorSubject() {
    return actorSubject;
  }

  public String actorIssuer() {
    return actorIssuer;
  }
}
