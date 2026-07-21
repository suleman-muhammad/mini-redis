package com.miniredis;

import com.miniredis.commands.CommandRouter;
import com.miniredis.data.Store;
import com.miniredis.persistence.AofWriter;
import com.miniredis.server.Server;

public class Main {
    public static void main(String[] args) {
        Store store = new Store();
        
        CommandRouter router = new CommandRouter(store);
        AofWriter.replay(router);
        AofWriter aof = new AofWriter();
        router.setAofWriter(aof);
        Server server = new Server(6380,router);
        server.start();
    }
}
