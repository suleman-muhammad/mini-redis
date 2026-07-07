package com.miniredis;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.common.base.Utf8;

public class RESP {
    private DataInputStream in;

    RESP(InputStream in){
        this.in = new DataInputStream(in);
    }


    public List<String> readCommand() throws IOException{
        String cur = Integer.toBinaryString(in.read());
        //TODO: 
        // handle result.
        switch (cur) {
            case "*":
                handleArrays();
                break;
            case "$":
                handleBulkString();
                break;
            default:
                throw new IOException();
        }
        return null;
    }

    public List<String> handleArrays() throws IOException{
        List<String> result = new ArrayList<>();
        int total = Integer.parseInt(Integer.toBinaryString(in.read()));
        //TODO:
        // discard the extra \r\n.
        for(int i = 0; i<total; i++){
            String cur = Integer.toBinaryString(in.read());
            switch (cur) {
                case "*":
                    handleArrays();
                    //TODO: handle the Array Result.
                    break;
                case "$":
                    result.add(handleBulkString());
                    break;
                default:
                    throw new IOException();
            } 
        }
        //TODO:
        // discard the extra \r\n.
        return null;    
    }

    public String handleBulkString() throws IOException{
        StringBuilder sb = new StringBuilder();
        int total = Integer.parseInt(Integer.toBinaryString(in.read()));
        //TODO:
        // discard the extra \r\n.
        for(int i = 0; i<total; i++){
            int cur = in.read();
            if (cur == -1){
                throw new IOException();
            }

            sb.append(Integer.toBinaryString(cur));
        }
        //TODO:
        // discard the extra \r\n.
        return null;
    }


}
