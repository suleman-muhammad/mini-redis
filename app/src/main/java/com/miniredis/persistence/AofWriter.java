package com.miniredis.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AofWriter {
    private final FileWriter fw;

    public AofWriter(String file) throws IOException{
        this.fw = new FileWriter(new File(".\\commands.txt"));
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

}
