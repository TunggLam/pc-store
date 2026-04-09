package com.example.pcstore.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.Nullable;

public class CustomCacheErrorHandler implements CacheErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, @Nullable Cache cache, @Nullable Object key) {
        logger.error("[CACHE GET][Exception: {}][Key: {}][Cache: {}]", exception.getMessage(), key, cache);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, @Nullable Cache cache, @Nullable Object key, Object value) {
        logger.error("[CACHE PUT][Exception: {}][Key: {}][Cache: {}]", exception.getMessage(), key, cache);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, @Nullable Cache cache, @Nullable Object key) {
        logger.error("[CACHE EVICT][Exception: {}][Key: {}][Cache: {}]", exception.getMessage(), key, cache);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, @Nullable Cache cache) {
        logger.error("[CACHE CLEAR][Exception: {}][Cache: {}]", exception.getMessage(), cache);
    }
}
