package com.miniredis.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.miniredis.data.Store;
import com.miniredis.resp.BulkString;
import com.miniredis.resp.RespInteger;
import com.miniredis.resp.Response;
import com.miniredis.resp.SimpleString;

public class CommandRouterTest {
    private Store store;
    private CommandRouter router;

    @BeforeEach 
    void setUp(){
        store = new Store();
        router = new CommandRouter(store);
    }

    @Test
    void pingTest() {
        Response r = router.handle(List.of("PING"), false);
        assertInstanceOf(SimpleString.class, r);
        assertEquals("+PONG\r\n", r.getResponse());
    }

    @Test
    void testSet(){
        Response r = router.handle(List.of("SET","foo","bar"),false);
        assertInstanceOf(SimpleString.class, r);
        assertEquals("+OK\r\n", r.getResponse());
    }


    @Test
    void testGet(){
        Response r = router.handle(List.of("SET","foo","bar"),false);
        r = router.handle(List.of("GET","foo"), false);
        assertInstanceOf(BulkString.class, r);
        assertEquals("$3\r\nbar\r\n", r.getResponse());
    }

    @Test
    void testDel(){
        Response r = router.handle(List.of("SET","foo","bar"),false);
        r = router.handle(List.of("DEL","foo"), false);
        assertInstanceOf(RespInteger.class, r);
        assertEquals(":1\r\n", r.getResponse());
    }

    @Test
    void testExists(){
        Response r = router.handle(List.of("SET","foo","bar"),false);
        r = router.handle(List.of("EXISTS","foo"), false);
        assertInstanceOf(RespInteger.class, r);
        assertEquals(":1\r\n", r.getResponse());
    }

    @Test
    void testTtl(){
        Response r = router.handle(List.of("SET","foo","bar"),false);
        r = router.handle(List.of("TTL","foo"), false);
        assertInstanceOf(RespInteger.class, r);
        assertEquals(":-1\r\n", r.getResponse());
    }




}
