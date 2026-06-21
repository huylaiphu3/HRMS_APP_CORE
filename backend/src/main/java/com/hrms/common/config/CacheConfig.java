package com.hrms.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    // NOTE: SimpleCacheManager only serves pre-declared caches — any new @Cacheable("name")
    // must be registered below, or Spring throws IllegalArgumentException at runtime.
    @Bean
    public CacheManager cacheManager() {
        var manager = new SimpleCacheManager();
        manager.setCaches(List.of(
            buildCache("systemConfig",   60,  TimeUnit.MINUTES, 100),
            buildCache("dashboardStats", 5,   TimeUnit.MINUTES, 100),
            buildCache("leaveBalances",  5,   TimeUnit.MINUTES, 1000)
        ));
        return manager;
    }

    private CaffeineCache buildCache(String name, long ttl, TimeUnit unit, int maxSize) {
        return new CaffeineCache(name,
            Caffeine.newBuilder()
                .expireAfterWrite(ttl, unit)
                .maximumSize(maxSize)
                .build());
    }
}
