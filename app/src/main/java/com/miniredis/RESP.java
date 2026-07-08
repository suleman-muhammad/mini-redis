package com.miniredis;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.imageio.IIOException;

import com.google.common.base.Utf8;

public class RESP {
    private DataInputStream in;

    RESP(InputStream in){
        this.in = new DataInputStream(in);
    }


    public List<String> readCommand() throws IOException{
        List<String> result = new ArrayList<>();
        String cur = Integer.toBinaryString(in.read());
        //TODO: 
        // handle result.
        switch (cur) {
            case "*":
                result = handleArrays();
                return result;
            case "$":
                result.add(handleBulkString());
                return result;
            default:
                throw new IOException();
        }
    }

    public List<String> handleArrays() throws IOException{
        List<String> result = new ArrayList<>();
        int total = Integer.parseInt(Integer.toBinaryString(in.read()));
        
        int cr = in.read();
        int lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new IOException("ERR UnKnown Pattern.");
        }
        
        for(int i = 0; i<total; i++){
            String cur = Integer.toBinaryString(in.read());
            switch (cur) {
                case "*":
                    List<String> r1 = handleArrays();
                    result.addAll(r1);
                    break;
                case "$":
                    result.add(handleBulkString());
                    break;
                default:
                    throw new IOException();
            } 
        }
        
        cr = in.read();
        lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new IOException("ERR UnKnown Pattern.");
        }

        return result;    
    }

    public String handleBulkString() throws IOException{
        StringBuilder sb = new StringBuilder();
        int total = Integer.parseInt(Integer.toBinaryString(in.read()));
        
        int cr = in.read();
        int lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new IOException("ERR UnKnown Pattern.");
        }

        for(int i = 0; i<total; i++){
            int cur = in.read();
            if (cur == -1){
                throw new IOException();
            }

            sb.append(Integer.toBinaryString(cur));
        }

        cr = in.read();
        lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new IOException("ERR UnKnown Pattern.");
        }

        return sb.toString();
    }


}
