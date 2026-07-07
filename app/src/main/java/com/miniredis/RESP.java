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
        switch (cur) {
            case "*":
                handleArrays();
                break;
            case "$":
                handleBulkString();
                break;
            default:
                throw new IOException();
                break;
        }
        return null;
    }


}
