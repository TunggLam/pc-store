package com.example.pcstore.configurations;

import com.example.pcstore.exception.handler.CustomCacheErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfiguration {

    private static final String SPACE = "_";

    @Value("${spring.cache.redis.key-prefix}")
    private String springCacheRedisKeyPrefix;

    @Value("${spring.cache.redis.use-key-prefix}")
    private boolean springCacheRedisUseKeyPrefix;

    @Value("${spring.cache.redis.time-to-live}")
    private long springCacheRedisTimeToLive;

    private CacheKeyPrefix cacheKeyPrefix;

    @PostConstruct
    private void onPostConstruct() {
        if (springCacheRedisKeyPrefix != null) {
            springCacheRedisKeyPrefix = springCacheRedisKeyPrefix.trim();
        }
        if (springCacheRedisUseKeyPrefix && springCacheRedisKeyPrefix != null
                && !springCacheRedisKeyPrefix.isEmpty()) {
            cacheKeyPrefix = cacheName -> springCacheRedisKeyPrefix + ":" + cacheName + ":";
        } else {
            cacheKeyPrefix = CacheKeyPrefix.simple();
        }
    }

    @Bean("cacheManager")
    public RedisCacheManager cacheManagerDefault(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .computePrefixWith(cacheKeyPrefix)
                        .entryTtl(Duration.ofSeconds(springCacheRedisTimeToLive))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)))
                )
                .transactionAware()
                .build();
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    @Bean("pcStoreKeyGenerator")
    public KeyGenerator keyGenerator() {
        return (Object target, Method method, Object... params) -> {
            /*-- Sử dụng tên phương thức làm khóa nếu không có tham số --*/
            if (params.length == 0) {
                return method.getName();
            }
            return StringUtils.arrayToDelimitedString(params, SPACE);
        };
    }
}