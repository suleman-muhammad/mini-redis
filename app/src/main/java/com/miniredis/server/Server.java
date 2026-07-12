package com.miniredis.server;

import java.net.ServerSocket;

public class Server {
    
    private final int port;
    private final ServerSocket server;


    public Server(int port){
        this.port = port;
        this.server = null;
    }

    
}
