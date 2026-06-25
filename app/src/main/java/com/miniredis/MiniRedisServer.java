package com.miniredis;
import java.net.*;

import java.io.*;
class MiniRedisServer {
    private Socket curClient;
    private ServerSocket redisServer;
    private PrintWriter output;
    private BufferedReader input;


    public boolean start(){
        try{
            redisServer = new ServerSocket(6380);
            System.out.println("Server is Started at Port: " + 6380);
            return true;
            
        }catch (Exception e){
            System.out.println("Cannot Start Server.");
            return false;
        }
    }

    public boolean connect(){
        try{
            System.out.println("Waiting for a client for Three way Handshake.");
            curClient = redisServer.accept();
            System.out.println("Client is Detected.");
            output = new PrintWriter(curClient.getOutputStream(),true);
            input = new BufferedReader(new InputStreamReader(curClient.getInputStream()));
            System.out.println("Client connected successfully at Port: " + curClient.getPort());
            return true;
        }catch (Exception e){
            return false;
        }
    }

}