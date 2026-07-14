package com.miniredis;

import com.miniredis.commands.CommandRouter;
import com.miniredis.data.Store;
import com.miniredis.server.Server;

public class Main {
    public static void main(String[] args) {
        Store store = new Store();
        CommandRouter router = new CommandRouter(store);
        Server server = new Server(6380,router);
        server.start();
    }
}
