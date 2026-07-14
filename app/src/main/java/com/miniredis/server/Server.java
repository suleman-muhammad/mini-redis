package com.miniredis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.miniredis.commands.CommandRouter;

public class Server {
    
    private final int port;
    private final CommandRouter cr;

    public Server(int port,CommandRouter cr){
        this.port = port;
        this.cr = cr;
    }

    public void start(){
        try(ServerSocket miniServer = new ServerSocket(this.port)){
            System.out.println("Server: Started at Port " + this.port);
            while(true){
                Socket client = miniServer.accept();
                ClientHandler handler = new ClientHandler(client,cr); // handle IOException.
                handler.handleClient();
            }
        }catch(IOException e){

        }
    }
}
