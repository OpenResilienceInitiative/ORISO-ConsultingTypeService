package de.caritas.cob.consultingtypeservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheManagerConfig {

  public static final String TENANT_CACHE = "tenantCache";
  public static final String CONSULTING_TYPE_GROUPS_CACHE = "consultingTypeGroupsCache";

  @Value("${cache.tenant.configuration.maxEntriesLocalHeap}")
  private long tenantMaxEntriesLocalHeap;

  @Value("${cache.tenant.configuration.eternal}")
  private boolean tenantEternal;

  @Value("${cache.tenant.configuration.timeToIdleSeconds}")
  private long tenantTimeToIdleSeconds;

  @Value("${cache.tenant.configuration.timeToLiveSeconds}")
  private long tenantTimeToLiveSeconds;

  @Value("${cache.groups.configuration.maxEntriesLocalHeap}")
  private long groupsMaxEntriesLocalHeap;

  @Value("${cache.groups.configuration.eternal}")
  private boolean groupsEternal;

  @Value("${cache.groups.configuration.timeToIdleSeconds}")
  private long groupsTimeToIdleSeconds;

  @Value("${cache.groups.configuration.timeToLiveSeconds}")
  private long groupsTimeToLiveSeconds;

  @Bean
  public CacheManager cacheManager() {
    var manager = new SimpleCacheManager();
    manager.setCaches(
        List.of(
            buildCache(
                TENANT_CACHE,
                tenantMaxEntriesLocalHeap,
                tenantEternal,
                tenantTimeToIdleSeconds,
                tenantTimeToLiveSeconds),
            buildCache(
                CONSULTING_TYPE_GROUPS_CACHE,
                groupsMaxEntriesLocalHeap,
                groupsEternal,
                groupsTimeToIdleSeconds,
                groupsTimeToLiveSeconds)));
    return manager;
  }

  private CaffeineCache buildCache(
      String name, long maxEntries, boolean eternal, long idleSeconds, long lifetimeSeconds) {
    var builder = Caffeine.newBuilder();
    // The existing configuration treats zero as unbounded.
    if (maxEntries != 0) {
      builder.maximumSize(maxEntries);
    }
    if (!eternal) {
      if (idleSeconds > 0) {
        builder.expireAfterAccess(Duration.ofSeconds(idleSeconds));
      }
      if (lifetimeSeconds > 0) {
        builder.expireAfterWrite(Duration.ofSeconds(lifetimeSeconds));
      }
    }
    return new CaffeineCache(name, builder.build(), true);
  }
}
