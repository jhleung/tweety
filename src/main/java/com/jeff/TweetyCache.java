package com.jeff;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TweetyCache {
    private HashMap<Object, Object> cache;
    private LinkedHashMap<Object, Long> createdAt;

    private Object tail;

    private long expireTine;

    public TweetyCache(long time) {
        expireTine = time;
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

    public Object get(Object key) { return cache.get(key); }
    public boolean contains(Object key) { return cache.containsKey(key); }
    public Object getTail() { return tail; }

    public void remove(Object key) {
        cache.remove(key);
        createdAt.remove(key);
    }
    public Set entrySet() { return cache.entrySet(); }
    public int size() { return cache.size(); }
    public boolean isEmpty() { return cache.size() == 0; }
    public boolean containsValue(Object o ) { return cache.containsValue(o); }
    public Collection<Object> values() { return cache.values(); }

    private void expireCacheEntries() {
        for (Map.Entry<Object, Long> entry : createdAt.entrySet()) {
            if (System.currentTimeMillis() -  entry.getValue() >= expireTine) {
                cache.remove(entry.getKey());
                createdAt.remove(entry.getKey());
            }
            else break;
        }
    }
}
