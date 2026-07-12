package com.miniredis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    private final int port;


    public Server(int port){
        this.port = port;
    }

    public void start(){
        try(ServerSocket miniServer = new ServerSocket(this.port)){
            System.out.println("Server: Started at Port " + this.port);
            while(true){
                Socket client = miniServer.accept();
                ClientHandler handler = new ClientHandler();
                handler.handleClient(client);
            }
        }catch(IOException e){

        }
    }

    
}
