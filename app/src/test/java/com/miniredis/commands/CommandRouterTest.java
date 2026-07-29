package com.miniredis.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.channels.Pipe.SourceChannel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.miniredis.data.Store;
import com.miniredis.resp.BulkString;
import com.miniredis.resp.ErrorString;
import com.miniredis.resp.NullString;
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

    @Test
    void setEdgeCaseTest(){
        Response r = router.handle(List.of("Set","foo"), false);
        assertInstanceOf(ErrorString.class,r);
    }

    @Test
    void setWithExCaseTest(){
        Response r = router.handle(List.of("Set","foo","bar","ex","5"), false);
        assertInstanceOf(SimpleString.class,r);
        try{
            Thread.sleep(5000);
        }catch (Exception e){
            System.out.println("Test Failed.");
        }
        r = router.handle(List.of("Get","foo"), false);
        assertInstanceOf(NullString.class, r);
    }

    @Test
    void setWithNegativeExCaseTest(){
        Response r = router.handle(List.of("Set","foo","bar","ex","-5"), false);
        assertInstanceOf(ErrorString.class,r);
    }

    @Test
    void setWithExpireCaseTest(){
        Response r = router.handle(List.of("Set","foo","bar"), false);
        assertInstanceOf(SimpleString.class,r);
        r = router.handle(List.of("Expire","foo","5"), false);
        assertInstanceOf(RespInteger.class,r);

        try{
            Thread.sleep(5000);
        }catch (Exception e){
            System.out.println("Test Failed.");
        }

        r = router.handle(List.of("Get","foo"), false);
        assertInstanceOf(NullString.class, r);
    }

    @Test
    void ttlForNonExistKeyTest(){
        Response r = router.handle(List.of("ttl","foo"), false);
        assertInstanceOf(RespInteger.class,r);
        assertEquals(":-2\r\n", r.getResponse());
    }

}
