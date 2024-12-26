package com.example.apijava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Object> hashGetByField(String key, String field) {
        return List.of(redisTemplate.opsForHash().get(key, field));
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(String key, int timeoutInDays) {
        redisTemplate.expire(key,timeoutInDays, TimeUnit.DAYS);
    }

    @Override
    public void hashSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public boolean exists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForHash().get(key, key);
    }

    @Override
    public Map<String, Object> getMap(String key) {
        return redisTemplate.<String, Object>opsForHash().entries(key);
    }

    @Override
    public Object hashGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public Set<String> getFields(String key) {
        return redisTemplate.<String, String>opsForHash().keys(key);
    }

    @Override
    public void del(String key) {

    }

    @Override
    public void del(String key, String field) {

    }

    @Override
    public void del(String key, List<String> fields) {

    }
}
