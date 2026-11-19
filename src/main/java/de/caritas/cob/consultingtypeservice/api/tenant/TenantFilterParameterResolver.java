package de.caritas.cob.consultingtypeservice.api.tenant;

import static de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver.TECHNICAL_TENANT_ID;

import de.caritas.cob.consultingtypeservice.api.model.TenantFilter;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Hibernate asks this for the {@link TenantFilter} argument on every query, so it always follows
 * the current thread. Fail-closed: without a tenant nothing matches, like the UserService.
 */
@Component
public class TenantFilterParameterResolver implements Supplier<Long> {

  static final Long UNRESTRICTED = TECHNICAL_TENANT_ID;
  static final Long NO_TENANT = -1L;

  // Field default: the filter stays active should Hibernate ever build this without Spring.
  @Value("${multitenancy.enabled:true}")
  private boolean multitenancyEnabled = true;

  @Override
  public Long get() {
    if (!multitenancyEnabled) {
      return UNRESTRICTED;
    }
    var currentTenant = TenantContext.getCurrentTenant();
    return currentTenant == null ? NO_TENANT : currentTenant;
  }
}
