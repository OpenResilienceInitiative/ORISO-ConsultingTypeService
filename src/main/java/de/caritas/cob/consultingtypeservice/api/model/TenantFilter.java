package de.caritas.cob.consultingtypeservice.api.model;

/**
 * The one Hibernate tenant filter of this service, declared on {@link TopicEntity}.
 *
 * <p>It is <b>auto-enabled</b>: every Hibernate session has it switched on from the moment it is
 * opened, whether or not a transaction is running. Its only parameter, {@code tenantId}, is not set
 * by anybody; Hibernate asks {@link
 * de.caritas.cob.consultingtypeservice.api.tenant.TenantFilterParameterResolver} for it each time a
 * query is rendered, so the value always follows the current {@code TenantContext}.
 *
 * <p>Before this, an aspect was meant to switch the filter on before each call into {@code
 * ..api.port..}. That package does not exist in this service, so the filter was never enabled and
 * every read that relied on it (the topics of a topic group) returned rows of every Träger.
 *
 * <p>Parameter {@code 0} lifts the restriction: the technical tenant, and every context the
 * resolver treats as unrestricted.
 */
public final class TenantFilter {

  public static final String NAME = "tenantFilter";
  public static final String PARAMETER = "tenantId";

  /** Rows of the current tenant only. */
  public static final String CONDITION = "(:tenantId = 0 OR tenant_id = :tenantId)";

  private TenantFilter() {}
}
