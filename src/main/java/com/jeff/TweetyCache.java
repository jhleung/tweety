package com.jeff;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TweetyCache {
    private HashMap<Object, Object> cache;
    private LinkedHashMap<Object, Long> createdAt;

    private Object tail;

    public TweetyCache() {
        cache = new HashMap<>();
        createdAt = new LinkedHashMap<>();
    }

    public void put(Object key, Object value) {
        if (createdAt.containsKey(key)) {
            createdAt.remove(key);
        }
        createdAt.put(key, System.currentTimeMillis());
        cache.put(key, value);

        tail = key;
        expireCacheEntries();
    }

    public Object get(String key) { return cache.get(key); }

    public boolean contains(String key) { return cache.containsKey(key); }

    public Object getTail() { return tail; }

    private void expireCacheEntries() {
        for (Map.Entry<Object, Long> entry : createdAt.entrySet()) {
            if (System.currentTimeMillis() -  entry.getValue() >= TimeUnit.DAYS.toMillis(1)) {
                cache.remove(entry.getKey());
                createdAt.remove(entry.getKey());
            }
            else
                break;
        }
    }
}
