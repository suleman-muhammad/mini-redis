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
    public static String FILE_PATH = ".\\commands.txt";
    private FileWriter fw;

    public AofWriter(String file) throws IOException{
        this.fw = new FileWriter(new File(FILE_PATH));
    }

    public synchronized void log(List<String> cmds) throws Exception{
        
        try{
            for (String cmd : cmds){
                fw.append(cmd);
                fw.append(" ");
            }
            fw.append("\n");
        }catch (IOException e){
            System.out.println("Writer: cannot write to Log file.");
            throw e;
        }
    }

    public void close() throws Exception{
        try{
            this.fw.close();
        }catch (IOException e){
            System.out.println("Writer: Error in closing the Log file.");
            throw e;
        }
    }

    public static void replay(CommandRouter cr) throws Exception{
        try(BufferedReader bf = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            while((line = bf.readLine()) != null){
                List<String> cmds = Arrays.asList(line.split(" "));
                cr.handle(cmds,false);
            }
        }catch(IOException e){
            System.out.println("Writer: cannot Execute Reply.");
            throw e;
        }

    }

}
