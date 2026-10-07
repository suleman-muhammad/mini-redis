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
    private final String FILE_PATH = "data/logs.txt";

    private Map<String,String> logs;

    public Optimizer(){
        this.logs = new HashMap<>();
    }

    public static void optimizeLogs(){
        Optimizer op = new Optimizer();
        op.optimize();
    }

    public void optimize(){
        File f = new File(FILE_PATH);
        if(!f.exists()){
            System.out.println("Optimizer: No Logs Found to Optimize.");
            return;
        }

        List<String> toLog = getOptimizedLogs();
        boolean result = log(toLog);

        if(!result){
            System.out.println("OPtimizer: Error Optimizing.");
            return ;
        }

        System.out.println("Optimizer: Optimization Successfull. You can work as you want.");
    } 

    private List<String> getOptimizedLogs(){
        this.logs = new HashMap<>();

        try(BufferedReader br = readFile()){
            String log;
            while((log = br.readLine()) != null){
                handleCommand(log);
            }
            return new ArrayList<>(logs.values());

        }catch (IOException e){
            System.out.println("OPtimizer Reader: Error reading logs File. Exisiting Now.");
        }catch (Exception e){
            System.out.println("Optimizer Reader: Error Optimizing logs. Exiting Now.");
        }
        return null;
    }

    private BufferedReader readFile()throws Exception{
        return new BufferedReader(new FileReader(FILE_PATH));
    }

    private boolean log(List<String> toLog){
        try(FileWriter fw = new FileWriter(FILE_PATH)){
            fw.write("");
            for (String line : toLog){
                fw.write(line + "\n");
            }

            return true;
        }catch (IOException e){
            System.out.println("Optimize Writer: Error Opening File for writing.");
            return  false;
        }catch (Exception e){
            System.out.println("optimize Writer: Error Writing to File.");
            return  false;
        }
    }
    
    private void handleCommand(String log){

        if(log.isEmpty()){
            return;
        }
        String[] parts = log.split("\t");
        String cmd = extractCommand(parts);
        switch (cmd.toLowerCase()) {
            case "set":
                handleSet(parts);
                break;
            case "incrby":
                handleIncrby(parts);
                break;
            case "expireat":
            case "expire":
            case "pexpire":
                handleExpire(parts);
                break;
            case "persist":
                handlePersist(parts);
                break;
            case "del":
                handleDel(parts);
                break;
            default:
                break;
        }

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
