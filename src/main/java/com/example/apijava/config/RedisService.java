package com.example.apijava.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {
    void set(String key, String value);
    void setTimeToLive(String key, int timeoutInDays);
    void hashSet(String key, String field, Object value);
    boolean exists(String key,String field);
    Object get(String key);
    Map<String, Object> getMap(String key);
    Object hashGet(String key, String field);
    List<Object> hashGetByField(String key, String field);
    Set<String> getFields(String key);
    void del(String key);
    void del(String key,String field);
    void del(String key, List<String> fields);
}
