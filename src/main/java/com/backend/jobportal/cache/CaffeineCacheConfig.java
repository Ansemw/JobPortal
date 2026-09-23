package com.backend.jobportal.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCache jobsCache = new CaffeineCache("jobs", Caffeine.newBuilder()
                .expireAfterWrite(10,TimeUnit.MINUTES)
                .maximumSize(5000)
                .build());

        CaffeineCache rolesCache = new CaffeineCache("roles", Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .maximumSize(100)
                .build());

        CaffeineCache companiesPublicCache = new CaffeineCache("companiesPublic", Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(500)
                .build());

        CaffeineCache companiesAdminCache = new CaffeineCache("companiesAdmin", Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(500)
                .build());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(rolesCache, companiesPublicCache, companiesAdminCache));
        return cacheManager;
    }
}
