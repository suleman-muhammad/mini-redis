package com.miniredis;

import com.miniredis.commands.CommandRouter;
import com.miniredis.data.Store;
import com.miniredis.persistence.AofWriter;
import com.miniredis.persistence.Optimizer;
import com.miniredis.server.Server;

public class Main {
    public static void main(String[] args) {
        int port = 6380;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException e) {

            }
        } else if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0].trim());
            } catch (NumberFormatException e) {
                
            }
        }

        Store store = new Store();
        
        CommandRouter router = new CommandRouter(store);
        Optimizer.optimizeLogs();
        long logs = AofWriter.replay(router);
        AofWriter aof = new AofWriter(logs);
        router.setAofWriter(aof);
        Server server = new Server(port, router);
        server.start();
    }
}
