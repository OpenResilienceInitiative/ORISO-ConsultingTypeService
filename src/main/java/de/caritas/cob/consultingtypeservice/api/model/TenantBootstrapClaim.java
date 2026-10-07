package de.caritas.cob.consultingtypeservice.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** The Mongo _id is the cross-instance arbitration point for initial task bootstrap only. */
@Document(collection = "tenant_bootstrap_claims")
@Getter
@AllArgsConstructor
public class TenantBootstrapClaim {
  @Id private final String tenantId;
  private final String attempt;
}
