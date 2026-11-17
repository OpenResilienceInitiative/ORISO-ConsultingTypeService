package de.caritas.cob.consultingtypeservice.api.tenant;

import java.util.function.Supplier;

/** Holds the tenant_id variable for ongoing thread assigned for HTTP request. */
public class TenantContext {

  private TenantContext() {}

  private static ThreadLocal<Long> currentTenant = new ThreadLocal<>();

  public static Long getCurrentTenant() {
    return currentTenant.get();
  }

  public static void setCurrentTenant(Long tenant) {
    currentTenant.set(tenant);
  }

  public static void clear() {
    currentTenant.remove();
  }

  /**
   * Runs the call with the given tenant and restores the previous tenant afterwards. A transaction
   * the call opens is committed (and flushed) before the previous tenant comes back.
   */
  public static <T> T callWithTenant(Long tenant, Supplier<T> call) {
    var previous = getCurrentTenant();
    setCurrentTenant(tenant);
    try {
      return call.get();
    } finally {
      if (previous == null) {
        clear();
      } else {
        setCurrentTenant(previous);
      }
    }
  }
}
