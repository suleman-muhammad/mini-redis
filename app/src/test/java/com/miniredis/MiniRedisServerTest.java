package com.miniredis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.miniredis.data.Store;

public class MiniRedisServerTest {
    
    @Test
    void testHandleCommand() {
        assertEquals("PONG", MiniRedisServer.handleCommand(Arrays.asList("PING"), null));
    }

    @Test
    void testHandleGet() {
        Store store = new Store();
        store.set("Chauhan", "Suleman");
        List<String> msgs = Arrays.asList("GET","Chauhan");
        assertEquals("Suleman", MiniRedisServer.handleGet(msgs, store));
    }

    @Test
    void testHandleSet() {
        Store store = new Store();
        store.set("Chauhan", "Suleman");
        List<String> msgs = Arrays.asList("GET","Suleman","Chauhan");
        assertEquals("Ok.", MiniRedisServer.handleSet(msgs, store));
    }
}
