package com.miniredis.data;

import java.util.Map;
import java.util.HashMap;

public class Store {
    private Map<String,String> data;
    public Store(){
        data = new HashMap<>();
    }
    public void set(String key, String val){
        data.put(key, val);
    }

    public String get(String key){
        return data.getOrDefault(key,null);
    }
}
