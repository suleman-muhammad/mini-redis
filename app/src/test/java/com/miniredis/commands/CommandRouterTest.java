package com.miniredis.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.miniredis.data.Store;
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
}
