package com.backend.jobportal.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    @Value("${cache.jobs.ttl-minutes:5}")
    private int jobsCacheTtlMinutes;

    @Value("${cache.jobs.max-size:1000}")
    private int jobsCacheMaxSize;

    @Value("${cache.companies.ttl-minutes:5}")
    private int companiesCacheTtlMinutes;

    @Value("${cache.companies.max-size:500}")
    private int companiesCacheMaxSize;

    @Value("${cache.roles.ttl-days:2}")
    private int rolesCacheTtlDays;

    @Value("${cache.roles.max-size:50}")
    private int rolesCacheMaxSize;


    @Bean
    public CacheManager cacheManager() {

        CaffeineCache jobsCache = new CaffeineCache("jobs", Caffeine.newBuilder()
                .expireAfterWrite(jobsCacheTtlMinutes,TimeUnit.MINUTES)
                .maximumSize(jobsCacheMaxSize)
                .build());

        CaffeineCache rolesCache = new CaffeineCache("roles", Caffeine.newBuilder()
                .expireAfterWrite(rolesCacheTtlDays, TimeUnit.DAYS)
                .maximumSize(rolesCacheMaxSize)
                .build());

        CaffeineCache companiesPublicCache = new CaffeineCache("companiesPublic", Caffeine.newBuilder()
                .expireAfterWrite(companiesCacheTtlMinutes, TimeUnit.MINUTES)
                .maximumSize(companiesCacheMaxSize)
                .build());

        CaffeineCache companiesAdminCache = new CaffeineCache("companiesAdmin", Caffeine.newBuilder()
                .expireAfterWrite(companiesCacheTtlMinutes, TimeUnit.MINUTES)
                .maximumSize(companiesCacheMaxSize)
                .build());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(rolesCache, companiesPublicCache, companiesAdminCache));
        return cacheManager;
    }
}
