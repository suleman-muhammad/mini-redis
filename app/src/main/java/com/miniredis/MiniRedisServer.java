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

    public boolean sendMessage(String msg){
        if(curClient == null){
            System.out.println("No Client is Connected.");
            return false;
        }
        output.println(msg);
        System.out.println("Message Sent Successfully to the Connected Client.");
        return true;
    }

    public String recieveMessage(){
        if(curClient == null){
            System.out.println("No Client is Connected.Returning NULL.");
            return null;
        }

        try{
            String msg = input.readLine();
            return msg;
        }catch (Exception e){
            System.out.println("Something bad happened while receiving from Client.Returning NULL.");
            return null;
        }
    }

    public void stop(){
        try{
            output.close();
            input.close();
            curClient.close();
            redisServer.close();
        }catch (Exception e){
            
        }
    }

    public static void main(String[] args) {
        MiniRedisServer redisServer = new MiniRedisServer();
        if(!redisServer.start()){
            System.out.println("Testing Failed.");
            return;
        }

        if(!redisServer.connect()){
            System.out.println("Could not Connect to clinet testing Failed.");
            return;
        }

        String msg = redisServer.recieveMessage();
        System.out.println("Received: "+ msg);
        System.out.println("Responding: " + msg);
        redisServer.sendMessage(msg);
        redisServer.stop();
    }
}