package com.miniredis.resp;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RespWriter {
    private final OutputStream output;

    public RespWriter(OutputStream output){
        this.output = output;
    }

    public void write(Response r) throws IOException{
        try{
            output.write(r.getResponse().getBytes(StandardCharsets.UTF_8));
            output.flush();
        }catch (IOException e){
            throw new IOException("Failed to send Response + " + r.getClass().getSimpleName(),e);
        }
    }
}
