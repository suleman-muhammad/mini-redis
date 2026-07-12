package com.miniredis.commands;

import java.util.List;

import com.miniredis.data.Store;

public class CommandRouter {
    
    private final Store store;

    public CommandRouter(Store store){
        this.store = store;
    }

    public String handleSet(List<String> cmds){
        if(cmds.size() != 3){
            return "Set Command only takes 2 arguments.";
        }
        String key = cmds.get(1);
        String val = cmds.get(2);
        if(key.length() == 0 || val.length() == 0){
            return "Key or value cannot be empty.";
        }

        store.set(key, val);
        return "Ok.";
    }

    public String handleGet(List<String> cmds){
        if(cmds.size() != 2){
            return "Get Command only takes 1 arguments.";
        }
        String key = cmds.get(1);
        if(key.length() == 0){
            return "Key cannot be empty.";
        }
        String val = store.get(key);
        if(val == null){
            return "No Value Found.";
        }
        return val;
    }


    public String handleCommand(List<String> cmds){

        if(cmds.size() == 0){
            return "Empty Command";
        }

        String cmd = cmds.getFirst().toUpperCase();
        return switch(cmd){
            case "SET" -> handleSet(cmds);
            case "GET" -> handleGet(cmds);
            case "PING" -> "PONG";
            default     -> "ERR unknown Command '" + cmd + "'"; 
        };
    }
}
