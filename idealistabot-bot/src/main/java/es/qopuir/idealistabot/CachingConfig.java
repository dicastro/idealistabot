package es.qopuir.idealistabot;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.config.CacheConfiguration;

@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport {

    @Bean
    @Override
    public CacheManager cacheManager() {
        CacheConfiguration dmiCitiModelRequestCacheConfiguration = new CacheConfiguration();
        dmiCitiModelRequestCacheConfiguration.setName("dmiCityModelRequest");
        dmiCitiModelRequestCacheConfiguration.setMaxEntriesLocalHeap(100);

        CacheConfiguration idealistaBuildingModelRequestCacheConfiguration = new CacheConfiguration();
        idealistaBuildingModelRequestCacheConfiguration.setName("idealistaBuildingModelRequest");
        idealistaBuildingModelRequestCacheConfiguration.setMaxEntriesLocalHeap(100);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(dmiCitiModelRequestCacheConfiguration);
        config.addCache(idealistaBuildingModelRequestCacheConfiguration);

        net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.newInstance(config);

        return new EhCacheCacheManager(cacheManager);
    }
}