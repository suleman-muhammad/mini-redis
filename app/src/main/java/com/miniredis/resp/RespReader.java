package com.miniredis.resp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
public class RespReader {
    private static final int MAX_BULK_STRING_LENGTH = 512 * 1024 * 1024;
    private static final int MAX_ARRAY_LENGTH = 1024 * 1024;

    private DataInputStream in;

    public RespReader(InputStream in){
        this.in = new DataInputStream(in);
    }


    public List<String> readCommand() throws IOException{
        List<String> result = new ArrayList<>();
        int cur = in.read();
        switch (cur) {
            case '*':
                result = handleArrays();
                return result;
            case '$':
                result.add(handleBulkString());
                return result;
            case -1:
                return null;
            default:
                throw new IOException("ERR Unknown Pattern.");
        }
    }

    public List<String> handleArrays() throws IOException,EOFException{
        List<String> result = new ArrayList<>();
        String len = readLength();
        int total;
        try{
            total = Integer.parseInt(len);
        }catch(NumberFormatException e){
            throw new IOException("Protocol error: invlaid length " + len,e);
        }

        if(total > MAX_ARRAY_LENGTH){
            throw new IOException(
                "PROTOCOL Error: Array length exceeds limit."
            );
        }
        if(total < 0){
            throw new IOException(
                "PROTOCOL Error: Array length must be Positive."
            );
        }

        int cr = in.read();
        int lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new IOException("ERR UnKnown Pattern.");
        }

        for(int i = 0; i<total; i++){
            int cur = in.read();
            switch (cur) {
                case '*':
                    List<String> r1 = handleArrays();
                    result.addAll(r1);
                    break;
                case '$':
                    result.add(handleBulkString());
                    break;
                case -1:
                    throw new EOFException();
                default:
                    throw new IOException("ERR Unknown Pattern.");
            } 
        }
        
        // cr = in.read();
        // lf = in.read();
        // if(cr != '\r' || lf != '\n'){
        //     throw new IOException("ERR UnKnown Pattern.");
        // }

        return result;    
    }

    public String handleBulkString() throws IOException,EOFException{
        StringBuilder sb = new StringBuilder();

        String len = readLength();
        int total;
        try{
            total = Integer.parseInt(len);
        }catch(NumberFormatException e){
            throw new IOException("Protocol error: invlaid length " + len,e);
        }
        
        if(total > MAX_BULK_STRING_LENGTH){
            throw new IOException(
                "PROTOCOL Error: bulk String length exceeds limit."
            );
        }

        if(total < -1){
            throw new IOException(
                "PROTOCOL Error: bulk String length cannot be less than -1."
            );
        }

        

        int cr = in.read();
        int lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new IOException("ERR UnKnown Pattern.");
        }

        for(int i = 0; i<total; i++){
            int cur = in.read();
            if (cur == -1){
                throw new EOFException();
            }

            sb.append(Character.toString(cur));
        }

        cr = in.read();
        lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new IOException("ERR UnKnown Pattern.");
        }

        return sb.toString();
    }

    private String readLength() throws IOException{
        StringBuilder sb = new StringBuilder();
        int b1 = in.read();
        int b2 = in.read();
        while(true){
            if(b1 == '\r' && b2 == '\n'){
                break;
            }
            sb.append(b1);
            b1 = b2;
            b2 = in.read();
        }
        return sb.toString();
    }


}
