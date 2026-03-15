package com.example.pcstore.redis;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("unchecked")
public class TokenRedisService extends RedisService{
    private static final String ACCESS_TOKEN_SUB_FIX = "_access_token_customers";
    private static final String REFRESH_TOKEN_SUB_FIX = "_refresh_token_customers";

    @Override
    public String get(String key){
        return (String) redisTemplate.opsForValue().get(key.toLowerCase() + ACCESS_TOKEN_SUB_FIX);
    }

    public void setRefreshToken(String key, String token, Date expiredDate) {
        long diff = expiredDate.getTime() - new Date().getTime();
        redisTemplate.opsForValue().set(key.toLowerCase() + REFRESH_TOKEN_SUB_FIX, token, diff, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String key){
        return (String) redisTemplate.opsForValue().get(key.toLowerCase() + REFRESH_TOKEN_SUB_FIX);
    }

    @Override
    public void set(String key, String token){
        redisTemplate.opsForValue().set(key.toLowerCase() + ACCESS_TOKEN_SUB_FIX, token);
    }
    @Override
    public void set(String key, String token, Date expiredDate) {
        long diff = expiredDate.getTime() - new Date().getTime();
        redisTemplate.opsForValue().set(key.toLowerCase() + ACCESS_TOKEN_SUB_FIX, token, diff, TimeUnit.MILLISECONDS);
    }

    public void remove(String key){
        redisTemplate.delete(key.toLowerCase() + ACCESS_TOKEN_SUB_FIX);
    }
}