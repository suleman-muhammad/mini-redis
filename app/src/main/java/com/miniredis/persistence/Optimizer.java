package com.miniredis.persistence;

import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Optimizer {
    private final String FILE_PATH = "data/commands.txt";

    private Map<String,String> logs;

    public Optimizer(){
        this.logs = new HashMap<>();
    }

    private void handleSet(String[] parts){
        String key = extractKey(parts);
        this.logs.put(key, String.join("\t",parts));       
    }

    private void handleIncrby(String[] parts){
        
        String key = extractKey(parts);
        long previousValue = 0;
        try{
            if(this.logs.containsKey(key)){
                String[] toLog  = this.logs.get(key).split("\t");
                previousValue = Long.valueOf(toLog[2]);
                long updatedvalue = previousValue + Long.valueOf(parts[2]);
                toLog[2] = String.valueOf(updatedvalue);
                this.logs.put(key, String.join("\t", toLog));
            } else {
                this.logs.put(key, "SET\t" + key + "\t" + parts[2]);
            }
        }catch(NumberFormatException e){
        
        }

    }
    private void handleExpire(String[] parts){
        String key = extractKey(parts);
        if(this.logs.containsKey(key)){
            String[] toLog = this.logs.get(key).split("\t");
            String value = extractValue(toLog);
            this.logs.put(key,"SET\t" + key + "\t" + value + "\tPXAT\t" + parts[2]);       
        }
    }

    private void handlePersist(String[] parts){
        String key = extractKey(parts);
        if(this.logs.containsKey(key)){
            String[] toLog = this.logs.get(key).split("\t");
            String value = extractValue(toLog);
            this.logs.put(key,"SET\t" + key + "\t" + value);
        }
    }

    private void handleDel(String[] parts){
        String key = extractKey(parts);
        this.logs.remove(key);
    }

    private String extractCommand(String[] parts){
        return parts[0];
    }
    private String extractKey(String[] parts){
        return parts[1];
    }
    private String extractValue(String[] parts){
        return parts[2];
    }

}
