package com.miniredis.commands;

import java.util.List;
import java.util.Set;

import com.miniredis.data.*;
import com.miniredis.persistence.AofWriter;
import com.miniredis.resp.*;

public class CommandRouter {
    private static final Set<String> LOG_COMMANDS = Set.of("SET","DEL","EXPIRE","PERSIST");
    private final Store store;
    private  AofWriter aof;

    public CommandRouter(Store store){
        this.store = store;
    }

    public void setAofWriter(AofWriter aof){
        this.aof = aof;
    }

    public Response handleSet(List<String> cmds){
        if(cmds.size() != 3 && cmds.size() != 5){
            return new ErrorString("Err Set Command only takes 2 arguments and optional(expiry seconds followed by 'ex').");
        }
        String key = cmds.get(1);
        String val = cmds.get(2);
        if(key.length() == 0 || val.length() == 0){
            return new ErrorString("Err Key or value cannot be empty.");
        }

        if(cmds.size() > 3){
            long t;
            try{
                t = Long.parseLong(cmds.getLast());
            }catch(NumberFormatException e){
                return new ErrorString("ERR TTL Value is not an integer.");
            }
            if(cmds.get(3).toLowerCase().equalsIgnoreCase("ex")){
                if(t <= 0){
                    return new ErrorString("ERR invalid expire time in 'set' command"); 
                }
                long expiresAt = System.currentTimeMillis() + (t * 1000);
                store.set(key, val,expiresAt);
            }else if(cmds.get(3).toLowerCase().equalsIgnoreCase("PXAT")){
                long expiresAt = t;
                store.set(key, val,expiresAt);
            }else{
                return new ErrorString("ERR unknown Command: '" + cmds.get(3) + "'");
            }

        }else
            store.set(key, val,0);
        return new SimpleString("OK");
    }

    public Response handleGet(List<String> cmds){
        if(cmds.size() != 2){
            return new ErrorString("Err Get Command only takes 1 arguments");
        }
        String key = cmds.get(1);
        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }
        String val = store.get(key);
        if(val == null){
            return NullString.instance;
        }
        return new BulkString(val);
    }

    private Response handleDel(List<String> cmds){
        if(cmds.size() != 2){
            return new ErrorString("Err Del Command only takes 1 arguments");
        }
        String key = cmds.get(1);
        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }
        boolean val = store.del(key);

        if(val){
            return new RespInteger(1);
        }
        return new RespInteger(0);
    }

    private Response handleExists(List<String> cmds){
        if(cmds.size() != 2){
            return new ErrorString("Err Exists Command only takes 1 arguments");
        }
        String key = cmds.get(1);
        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }
        boolean val = store.exists(key);

        if(val){
            return new RespInteger(1);
        }
        return new RespInteger(0);
    }

    private Response handleTTL(List<String> cmds){
        if(cmds.size() != 2){
            return new ErrorString("Err TTL Command only takes 1 arguments");
        }
        String key = cmds.get(1);
        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }
        
        long ttl = store.ttl(key);
        return new RespInteger((int) ttl);

    }

    private Response handleExpire(List<String> cmds){
        if(cmds.size() != 3){
            return new ErrorString("Err 'EXPIRE' Command only takes 2 arguments");
        }
        
        String key = cmds.get(1);

        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }

        
        long t;
        try{
            t = Long.parseLong(cmds.getLast());
        }
        catch (NumberFormatException e){
            return new ErrorString("ERR TTL Value is not an integer.");
        }

        if(t <= 0){
            return new RespInteger(store.del(key) ? 1 : 0); // del if already exits
        }

        long expiresAt = System.currentTimeMillis() + (t * 1000);

        
      
        return new RespInteger(store.expire(key,expiresAt) ? 1 : 0);
    }

    private Response handleExpireAt(List<String> cmds){
        if(cmds.size() != 3){
            return new ErrorString("Err 'EXPIRE' Command only takes 2 arguments");
        }
        
        String key = cmds.get(1);

        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }

        
        long t;
        try{
            t = Long.parseLong(cmds.getLast());
        }
        catch (NumberFormatException e){
            return new ErrorString("ERR TTL Value is not an integer.");
        }

        if(t <= 0){
            return new RespInteger(store.del(key) ? 1 : 0); // del if already exits
        }        
        return new RespInteger(store.expire(key,t) ? 1 : 0);
    }

    private Response handlePersist(List<String> cmds){
        if(cmds.size() != 2){
            return new ErrorString("Err Persist Command only takes 1 arguments");
        }
        String key = cmds.get(1);
        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }
        
        return new RespInteger(store.persist(key));

    }


    public Response handle(List<String> cmds,boolean toLog){

        if(cmds.isEmpty()){
            return new ErrorString("ERR Empty Command");
        }

        String cmd = cmds.getFirst().toUpperCase();
        Response r =  switch(cmd){
            case "SET" -> handleSet(cmds);
            case "GET" -> handleGet(cmds);
            case "DEL" -> handleDel(cmds);
            case "EXISTS" -> handleExists(cmds);
            case "PING" -> new SimpleString("PONG");
            case "TTL" -> handleTTL(cmds);
            case "EXPIRE" -> handleExpire(cmds);
            case "PERSIST" -> handlePersist(cmds);
            case "EXPIREAT" -> handleExpireAt(cmds);
            default     -> new ErrorString("ERR unknown Command '" + cmd + "'"); 
        };

        if(toLog && LOG_COMMANDS.contains(cmd) && !(r instanceof ErrorString)){
            try{
                aof.log(this.convertToAbsoluteExpiry(cmds));
            }catch(Exception e){

            }
        }
        return r;
    }


    public void startSweeping(){
        store.startSweeping();
    }

    public void stopSweeping(){
        store.stopSweeping();
    }
    public void closeLogs(){
        this.aof.close();
    }
    public List<String> convertToAbsoluteExpiry(List<String> cmds){
        if(cmds.size() == 5 && cmds.get(3).equalsIgnoreCase("EX")){
            long seconds = Long.parseLong(cmds.getLast());
            long absoluteMs = System.currentTimeMillis() + (seconds * 1000);
            cmds.removeLast();
            cmds.removeLast();
            cmds.add("PXAT");
            cmds.add(String.valueOf(absoluteMs));
            return cmds;
        }

        if(cmds.size() == 3 && cmds.getFirst().equalsIgnoreCase("expire")){
            long seconds = Long.parseLong(cmds.getLast());
            long absoluteMs = System.currentTimeMillis() + (seconds * 1000);
            return List.of("EXPIREAT", cmds.get(1), String.valueOf(absoluteMs));
        }
        return cmds;
    }
}
