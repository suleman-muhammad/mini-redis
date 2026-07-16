package com.miniredis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.miniredis.commands.CommandRouter;

public class Server {
    
    private final int port;
    private final CommandRouter cr;
    private final ExecutorService es;

    public Server(int port,CommandRouter cr){
        this.port = port;
        this.cr = cr;
        es = Executors.newFixedThreadPool(50);
    }

    public void start(){
        try(ServerSocket miniServer = new ServerSocket(this.port)){
            System.out.println("Server: Started at Port " + this.port);
            while(true){
                Socket client = miniServer.accept();
                try{
                    ClientHandler handler = new ClientHandler(client,cr);
                    es.submit(() -> {handler.handleClient();});
                }catch (Exception e){

                }
            }
        }catch(IOException e){
            System.out.println("Server: cannot Start the server.");
        }finally{
            es.shutdownNow();
            System.out.println("Server: Shut down Complete.");
        }
    }


    private void shutdown(){
        System.out.println("Server: Shutting down......");
        es.shutdown();
        try{
            if(!es.awaitTermination(10, TimeUnit.SECONDS)){
                es.shutdownNow();
            }
        }catch (InterruptedException e){
            es.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void registerShutdown(){
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }
}
