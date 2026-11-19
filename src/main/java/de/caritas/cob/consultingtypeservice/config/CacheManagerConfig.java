package de.caritas.cob.consultingtypeservice.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueRetrievalException;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.cache.support.AbstractValueAdaptingCache;
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
  public CacheManager cacheManager(net.sf.ehcache.CacheManager ehCacheManager) {
    return new EhCache2CacheManager(ehCacheManager);
  }

  @Bean(destroyMethod = "shutdown")
  public net.sf.ehcache.CacheManager ehCacheManager() {
    var config = new net.sf.ehcache.config.Configuration();
    config.addCache(buildTenantCacheConfiguration());
    config.addCache(buildConsultingTypeGroupsCacheConfiguration());
    return net.sf.ehcache.CacheManager.newInstance(config);
  }

  private CacheConfiguration buildTenantCacheConfiguration() {
    var tenantCacheConfiguration = new CacheConfiguration();
    tenantCacheConfiguration.setName(TENANT_CACHE);
    tenantCacheConfiguration.setMaxEntriesLocalHeap(tenantMaxEntriesLocalHeap);
    tenantCacheConfiguration.setEternal(tenantEternal);
    tenantCacheConfiguration.setTimeToIdleSeconds(tenantTimeToIdleSeconds);
    tenantCacheConfiguration.setTimeToLiveSeconds(tenantTimeToLiveSeconds);
    return tenantCacheConfiguration;
  }

  private CacheConfiguration buildConsultingTypeGroupsCacheConfiguration() {
    var tenantCacheConfiguration = new CacheConfiguration();
    tenantCacheConfiguration.setName(CONSULTING_TYPE_GROUPS_CACHE);
    tenantCacheConfiguration.setMaxEntriesLocalHeap(groupsMaxEntriesLocalHeap);
    tenantCacheConfiguration.setEternal(groupsEternal);
    tenantCacheConfiguration.setTimeToIdleSeconds(groupsTimeToIdleSeconds);
    tenantCacheConfiguration.setTimeToLiveSeconds(groupsTimeToLiveSeconds);
    return tenantCacheConfiguration;
  }

  private static final class EhCache2CacheManager extends AbstractCacheManager {

    private final net.sf.ehcache.CacheManager cacheManager;

    private EhCache2CacheManager(net.sf.ehcache.CacheManager cacheManager) {
      this.cacheManager = cacheManager;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
      var caches = new ArrayList<Cache>();
      for (String name : cacheManager.getCacheNames()) {
        caches.add(new EhCache2Cache(cacheManager.getEhcache(name)));
      }
      return caches;
    }

    @Override
    protected Cache getMissingCache(String name) {
      Ehcache cache = cacheManager.getEhcache(name);
      return cache == null ? null : new EhCache2Cache(cache);
    }
  }

  private static final class EhCache2Cache extends AbstractValueAdaptingCache {

    private final Ehcache cache;

    private EhCache2Cache(Ehcache cache) {
      super(true);
      this.cache = cache;
    }

    @Override
    public String getName() {
      return cache.getName();
    }

    @Override
    public Object getNativeCache() {
      return cache;
    }

    @Override
    protected Object lookup(Object key) {
      Element element = cache.get(key);
      return element == null ? null : element.getObjectValue();
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
      Element element = cache.get(key);
      if (element != null) {
        @SuppressWarnings("unchecked")
        T value = (T) fromStoreValue(element.getObjectValue());
        return value;
      }
      try {
        T value = valueLoader.call();
        put(key, value);
        return value;
      } catch (Exception ex) {
        throw new ValueRetrievalException(key, valueLoader, ex);
      }
    }

    @Override
    public void put(Object key, Object value) {
      cache.put(new Element(key, toStoreValue(value)));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
      Element existing = cache.putIfAbsent(new Element(key, toStoreValue(value)));
      return toValueWrapper(existing == null ? null : existing.getObjectValue());
    }

    @Override
    public void evict(Object key) {
      cache.remove(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
      return cache.remove(key);
    }

    @Override
    public void clear() {
      cache.removeAll();
    }

    @Override
    public boolean invalidate() {
      cache.removeAll();
      return true;
    }
  }
}
