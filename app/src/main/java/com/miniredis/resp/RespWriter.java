package com.miniredis.resp;

import java.io.OutputStream;

public class RespWriter {
    private final OutputStream output;

    public RespWriter(OutputStream output){
        this.output = output;
    }

    
}
