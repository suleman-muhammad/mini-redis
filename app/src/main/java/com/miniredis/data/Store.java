package com.miniredis.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
    private final Map<String,String> data;
    public Store(){
        data = new ConcurrentHashMap<>();
    }
    public void set(String key, String val){
        data.put(key, val);
    }

    public String get(String key){
        return data.getOrDefault(key,null);
    }

    public boolean del(String key){
        return data.remove(key) != null;
    }

    public boolean exists(String key){
        return data.containsKey(key);
    }
}
