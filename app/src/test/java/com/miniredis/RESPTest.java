package com.miniredis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RESPTest {

    private RESP resp;
    @BeforeEach
    public void init(){
        resp = new RESP(System.in);
    }

    @Test
    void testHandleArrays() {

    }

    @Test
    void testHandleBulkString() throws IOException{
        assertEquals("Suleman", resp.handleBulkString());
    }

    @Test
    void testReadCommand() {

    }
}
