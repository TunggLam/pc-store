package com.example.pcstore.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {
    @SuppressWarnings("rawtypes")
    @Autowired
    protected RedisTemplate redisTemplate;

    @SuppressWarnings("unchecked")
    public void set(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key){
        var data = redisTemplate.opsForValue().get(key);
        return data != null ? (String) data : null;
    }

    @SuppressWarnings("unchecked")
    public boolean deleteKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @SuppressWarnings("unchecked")
    public void set(String key, String value, Date expiredDate){
        long diff = expiredDate.getTime() - new Date().getTime();
        redisTemplate.opsForValue().set(key, value, diff, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    public void set(String key, String value, Long expired) {
        redisTemplate.opsForValue().set(key,value, expired, TimeUnit.SECONDS);
    }
}
