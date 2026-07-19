package com.miniredis.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
    private final Map<String,Value> data;
    public Store(){
        data = new ConcurrentHashMap<>();
    }
    public void set(String key, String val,long time){
        Value v = new Value(val,time);
        data.put(key, v);
    }

    public String get(String key){
        Value v = data.computeIfPresent(key, (k,current) -> 
            current.isExpired() ? null : current
        );

        return v == null ? null : v.val();
    }

    public boolean del(String key){
        return data.remove(key) != null;
    }

    public boolean exists(String key){
        return data.containsKey(key);
    }

    public boolean expire(String key,long t){
        Value v = data.computeIfPresent(key, (k,curr) ->
            new Value(curr.val(),t)
        );
        return v != null;
    }
    public long ttl(String key){
        Value v = data.compute(key, (k,curr) ->
            curr
        );
        if(v == null) return -1;
        if(v.expiresAtMillis() == 0) return 0;
        return ((v.expiresAtMillis() - System.currentTimeMillis())/1000);
    }
}
