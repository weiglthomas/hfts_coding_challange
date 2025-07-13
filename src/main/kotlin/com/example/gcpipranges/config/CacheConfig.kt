package com.example.gcpipranges.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    
    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager("gcp-ip-ranges")
        cacheManager.setCaffeine(caffeineBuilder())
        return cacheManager
    }
    
    fun caffeineBuilder(): Caffeine<Any, Any> = 
        Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(500)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .weakKeys()
            .recordStats()
}
