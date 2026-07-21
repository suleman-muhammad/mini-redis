package com.miniredis.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Store {
    private final Map<String,Value> data;
    private final ScheduledExecutorService sweeperPool;
    public Store(){
        data = new ConcurrentHashMap<>();
        sweeperPool = Executors.newSingleThreadScheduledExecutor();
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
        int[] result = {0}; 
        Value v = data.computeIfPresent(key, (k,val) ->
            {
                if(val.isExpired()){
                    result[0] = 0;
                    return null;
                }else{
                    result[0] = 1;
                }
                return val;
            }
        );

        return result[0] == 1;
    }

    public boolean expire(String key,long t){
        Value v = data.computeIfPresent(key, (k,curr) ->
            new Value(curr.val(),t)
        );
        return v != null;
    }
    public long ttl(String key){
        Value v = data.computeIfPresent(key, (k,curr) ->
            {
                if(curr.isExpired()){
                    return null;
                }
                return curr;
            }
        );
        if(v == null) return -2;
        if(v.expiresAtMillis() == 0) return -1;
        return ((v.expiresAtMillis() - System.currentTimeMillis())/1000);
    }

    public int persist(String key){
        boolean[] exists = {true};
        Value v = data.computeIfPresent(key, (k,curr) -> {
                if(curr.expiresAtMillis() != 0){
                    exists[0] = false;
                    return new Value(curr.val(),0);
                }
                return curr;
            }
        );
        if(v == null || exists[0]) return 0;
        return 1;
    }

    public void startSweeping(){
        sweeperPool.scheduleAtFixedRate(this::sweep, 1, 1, TimeUnit.SECONDS);
    }

    private void sweep(){
        try{
            int[] removed = {0};
            for(String key : data.keySet()){
                data.computeIfPresent(key, (k,v) ->
                    {
                        if(v.isExpired()){
                            removed[0]++;
                            return null;
                        }
                        return v;
                    }
                );
            }
            if(removed[0] > 0)System.out.println("Sweeper: removed " + removed[0] + " entries.");
        }catch(Exception e){
            System.err.println("Sweeper: Error in Sweeping.");
        }
    }

    public void stopSweeping(){
        sweeperPool.shutdown();
    }
}
