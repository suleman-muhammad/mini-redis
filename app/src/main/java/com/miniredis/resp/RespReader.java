package com.miniredis.resp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.miniredis.exceptions.ProtocolException;
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
            throw new ProtocolException("Protocol error: invlaid length " + len);
        }

        if(total > MAX_ARRAY_LENGTH){
            throw new ProtocolException(
                "PROTOCOL Error: Array length exceeds limit " + total  
            );
        }
        if(total < 0){
            throw new ProtocolException(
                "PROTOCOL Error: Array length must be Positive " + total
            );
        }

        int cr = in.read();
        int lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new ProtocolException("Protocol error: Expected CRLF after length of Array, got \" + cr + \"  \" + lf");
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
                    throw new EOFException("Protocol Error: Client Disconnected.");
                default:
                    throw new ProtocolException("Protocol error: unknown byte + " + cur);
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
            throw new ProtocolException("Protocol error: invlaid length " + len);
        }
        
        if(total > MAX_BULK_STRING_LENGTH){
            throw new ProtocolException(
                "PROTOCOL Error: bulk String length exceeds limit " + total
            );
        }

        if(total < -1){
            throw new ProtocolException(
                "PROTOCOL Error: bulk String length is invalid " + total
            );
        }

        int cr = in.read();
        int lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new ProtocolException("ERR UnKnown Pattern: Expected CRLF after length of Bulk String, got \" + cr + \"  \" + lf");
        }

        // for(int i = 0; i<total; i++){
        //     int cur = in.read();
        //     if (cur == -1){
        //         throw new EOFException();
        //     }

        //     sb.append(Character.toString(cur));
        // }

        byte[] data = new byte[total];
        try{
            in.readFully(data);
        }catch (EOFException e){
            throw new EOFException("Protocol error: Expected " + total + " bytes but coulc not fetch.");
        }

        for(byte cur : data){
            sb.append(Character.toString(cur));
        }

        cr = in.read();
        lf = in.read();
        if(cr != '\r' || lf != '\n'){
            throw new ProtocolException("ERR UnKnown Pattern: Expected CRLF after Bulk String, got " + cr + "  " + lf);
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
