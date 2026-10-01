package de.caritas.cob.consultingtypeservice.api.model;

/**
 * Auto-enabled so every session filters topics by tenant, whichever path reads them. Argument
 * {@code 0} = unrestricted, {@code -1} = no tenant (matches nothing).
 */
public final class TenantFilter {

  public static final String NAME = "tenantFilter";
  public static final String PARAMETER = "tenantId";

  /** Rows of the current tenant only. */
  public static final String CONDITION = "(:tenantId = 0 OR tenant_id = :tenantId)";

  private TenantFilter() {}
}
