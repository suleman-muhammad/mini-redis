package com.miniredis.server;

import java.net.Socket;

public class ClientHandler {
    
    public void handleClient(Socket client){
        System.out.println("Server: Connected a new Client.");
        
    }
}
