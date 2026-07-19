package com.miniredis.commands;

import java.util.List;

import com.miniredis.data.*;
import com.miniredis.resp.*;

public class CommandRouter {
    
    private final Store store;

    public CommandRouter(Store store){
        this.store = store;
    }

    public Response handleSet(List<String> cmds){
        if(cmds.size() != 3 || cmds.size() != 5){
            return new ErrorString("Err Set Command only takes 2 arguments.");
        }
        String key = cmds.get(1);
        String val = cmds.get(2);
        if(key.length() == 0 || val.length() == 0){
            return new ErrorString("Err Key or value cannot be empty.");
        }

        if(cmds.size() > 3){
            if(cmds.get(3).toLowerCase() != "ex"){
                return new ErrorString("ERR unknown Command: '" + cmds.get(3) + "'");
            }
            try{
                long t = Long.parseLong(cmds.getLast());
                if(t <= 0){
                    return new ErrorString("ERR invalid expire time in 'set' command"); 
                }
                store.set(key, val, t);
            }catch(NumberFormatException e){
                return new ErrorString("ERR TTL Value is not an integer.");
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
            return new ErrorString("Err Del Command only takes 1 arguments");
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
        if(cmds.size() != 3){
            return new ErrorString("Err TTL Command only takes 2 arguments");
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

    private Response handleExpire(List<String> cmds){
        if(cmds.size() != 3){
            return new ErrorString("Err 'EXPIRE' Command only takes 2 arguments");
        }
        
        String key = cmds.get(1);
        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty");
        }

        if(!store.exists(key)){
            return new RespInteger(0);
        }
        
        int t = Integer.parseInt(cmds.getLast());
        if(t <= 0){
            store.del(key); // del if already exits, dont care about the return value.
        }

        
        return new RespInteger(0);
    }


    public Response handle(List<String> cmds){

        if(cmds.isEmpty()){
            return new ErrorString("ERR Empty Command");
        }

        String cmd = cmds.getFirst().toUpperCase();
        return switch(cmd){
            case "SET" -> handleSet(cmds);
            case "GET" -> handleGet(cmds);
            case "DEL" -> handleDel(cmds);
            case "EXISTS" -> handleExists(cmds);
            case "PING" -> new SimpleString("PONG");
            case "TTL" -> handleTTL(cmds);
            default     -> new ErrorString("ERR unknown Command '" + cmd + "'"); 
        };
    }
}
