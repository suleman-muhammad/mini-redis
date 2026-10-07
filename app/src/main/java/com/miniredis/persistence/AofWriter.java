package com.miniredis.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.miniredis.commands.CommandRouter;

public class AofWriter {
    public static String FILE_PATH = "data/logs.txt";
    public static final long LOGS_LIMIT = 100_000;
    private FileWriter fw;
    private ConcurrentLinkedQueue<List<String>> bakcupLogs;
    private volatile PersistenceState currentState;
    private ExecutorService optimizerService;
    private AtomicLong currLogs;
    
    public AofWriter(long currLogs){
        if(this.open()){
            bakcupLogs = new ConcurrentLinkedQueue<>();
            currentState = PersistenceState.LOGGING; 
            optimizerService = Executors.newSingleThreadExecutor();
            this.currLogs = new AtomicLong(currLogs);
        }
    }

    public boolean open(){
        try{
            new File(FILE_PATH).getParentFile().mkdirs();
            this.fw = new FileWriter(new File(FILE_PATH),true);
            return true;
            
        }catch (IOException e){
            System.out.println("Writer: error in Opening File." + e.getMessage());
            return false;
        }
    }

    public synchronized void log(List<String> cmds){
        try{
            fw.append(String.join("\t", cmds));
            fw.append("\n");
            fw.flush();
        }catch (IOException e){
            System.out.println("Writer: cannot write to Log file." + e.getMessage());
        }
    }

    public void close(){
        try{
            this.fw.close();
        }catch (IOException e){
            System.out.println("Writer: Error in closing the Log file." + e.getMessage());
        }
    }

    public static void replay(CommandRouter cr){
        File f = new File(FILE_PATH);
        if(!f.exists()){
            System.out.println("Writer: No AOF file found Starting fresh.");
            return;
        }

        try(BufferedReader bf = new BufferedReader(new FileReader(new File(FILE_PATH)))){
            String line;
            while((line = bf.readLine()) != null){
                List<String> cmds = Arrays.asList(line.split("\t"));
                cr.handle(cmds,false);
            }
            System.out.println("Writer: Replay Complete.");
        }catch(IOException e){
            System.out.println("Writer: cannot Execute Reply.");
        }
        return;
    }

}
