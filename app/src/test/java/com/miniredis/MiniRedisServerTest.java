package com.miniredis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MiniRedisServerTest {
    
    @Test
    void testHandleCommand() {
        assertEquals("PONG", MiniRedisServer.handleCommand("PING", null));
    }

    @Test
    void testHandleGet() {
        Store store = new Store();
        store.set("Chauhan", "Suleman");
        String[] msgs = {"GET","Chauhan"};
        assertEquals("Suleman", MiniRedisServer.handleGet(msgs, store));
    }

    @Test
    void testHandleSet() {
        Store store = new Store();
        store.set("Chauhan", "Suleman");
        String[] msgs = {"SET","Chauhan","Suleman"};
        assertEquals("Ok.", MiniRedisServer.handleSet(msgs, store));
    }
}
