package com.miniredis.commands;

import java.util.List;

import com.miniredis.data.Store;
import com.miniredis.resp.BulkString;
import com.miniredis.resp.ErrorString;
import com.miniredis.resp.NullString;
import com.miniredis.resp.Response;
import com.miniredis.resp.SimpleString;

public class CommandRouter {
    
    private final Store store;

    public CommandRouter(Store store){
        this.store = store;
    }

    public Response handleSet(List<String> cmds){
        if(cmds.size() != 3){
            return new ErrorString("Err Set Command only takes 2 arguments.");
        }
        String key = cmds.get(1);
        String val = cmds.get(2);
        if(key.length() == 0 || val.length() == 0){
            return new ErrorString("Err Key or value cannot be empty.");
        }

        store.set(key, val);
        return new SimpleString("Ok.");
    }

    public Response handleGet(List<String> cmds){
        if(cmds.size() != 2){
            return new ErrorString("Err Get Command only takes 1 arguments.");
        }
        String key = cmds.get(1);
        if(key.length() == 0){
            return new ErrorString("Err Key cannot be empty.");
        }
        String val = store.get(key);
        if(val == null){
            return NullString.instance;
        }
        return new BulkString(val);
    }


    public Response handle(List<String> cmds){

        if(cmds.size() == 0){
            return new ErrorString("ERR Empty Command");
        }

        String cmd = cmds.getFirst().toUpperCase();
        return switch(cmd){
            case "SET" -> handleSet(cmds);
            case "GET" -> handleGet(cmds);
            case "PING" -> new SimpleString("PONG");
            default     -> new ErrorString("ERR unknown Command '" + cmd + "'"); 
        };
    }
}
