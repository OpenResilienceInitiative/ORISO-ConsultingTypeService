package de.caritas.cob.consultingtypeservice.api.tenant;

import static de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver.TECHNICAL_TENANT_ID;

import de.caritas.cob.consultingtypeservice.api.model.TenantFilter;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Supplies the {@code tenantId} argument of the auto-enabled {@link TenantFilter}. Hibernate calls
 * it whenever a filtered query or a load by id runs, in whatever session that happens, so the
 * argument is always the tenant of the current thread at that moment.
 *
 * <p>Returns {@code 0} (no restriction) when
 *
 * <ul>
 *   <li>multitenancy is switched off — the single-tenant deployments never filtered;
 *   <li>the current tenant is the technical tenant {@code 0};
 *   <li>no tenant is set: system threads and the routes that {@code HttpTenantFilter} deliberately
 *       skips ({@code /settings}, {@code /settingsadmin}, actuator). Every other request passes
 *       {@code HttpTenantFilter}, which either sets a tenant or rejects the request.
 * </ul>
 *
 * <p>Hibernate obtains this bean through Spring's bean container, so each application context uses
 * its own {@code multitenancy.enabled} value. The field default keeps the filter active should
 * Hibernate ever instantiate the class without Spring.
 */
@Component
public class TenantFilterParameterResolver implements Supplier<Long> {

  static final Long UNRESTRICTED = TECHNICAL_TENANT_ID;

  @Value("${multitenancy.enabled:true}")
  private boolean multitenancyEnabled = true;

  @Override
  public Long get() {
    if (!multitenancyEnabled) {
      return UNRESTRICTED;
    }
    var currentTenant = TenantContext.getCurrentTenant();
    return currentTenant == null ? UNRESTRICTED : currentTenant;
  }
}
