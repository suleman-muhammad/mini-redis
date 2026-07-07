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
        Byte[] curBytes = new Byte[][1];
        curBytes[0] = 

        String curChar = new String(curBytes,StandardCharsets.UTF_8);
        return null;
    }


}
