package com.miniredis.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.miniredis.commands.CommandRouter;

public class AofWriter {
    public static String FILE_PATH = "data/commands.txt";
    private FileWriter fw;

    public AofWriter(){
        try{
            new File(FILE_PATH).getParentFile().mkdirs();
            this.fw = new FileWriter(new File(FILE_PATH),true);
        }catch (IOException e){
            System.out.println("Writer: error in Constructor." + e.getMessage());
        }
    }

    public synchronized void log(List<String> cmds){
        
        try{
            for (String cmd : cmds){
                fw.append(cmd);
                fw.append(" ");
            }
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
                List<String> cmds = Arrays.asList(line.split(" "));
                cr.handle(cmds,false);
            }
            System.out.println("Writer: Replay Complete.");
        }catch(IOException e){
            System.out.println("Writer: cannot Execute Reply.");
        }

    }

}
