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

    public static String handleSet(String[] cmds, Store store){
        if(cmds.length != 3){
            return "Set Command only takes 2 arguments.";
        }
        String key = cmds[1];
        String val = cmds[2];
        if(key.length() == 0 || val.length() == 0){
            return "Key or value cannot be empty.";
        }

        store.set(cmds[1], cmds[2]);
        return "Ok.";
    }
    public static String handleGet(String[] cmds, Store store){
        
        return null;
    }


    public static String handleCommand(String msg,Store store){
        String[] cmds = msg.split(" ");
        if(cmds.length == 0){
            return "Empty Command";
        }

        String cmd = cmds[0].toUpperCase();
        return switch(cmd){
            case "SET" -> handleSet(cmds,store);
            case "GET" -> handleGet(cmds,store);
            case "PING" -> "PONG";
            default     -> "ERR unknown Command '" + cmd + "'"; 
        };
    }

    public static void main(String[] args) {
        // MiniRedisServer redisServer = new MiniRedisServer();
        // if(!redisServer.start()){
        //     System.out.println("Program Failed.");
        //     return;
        // }
        // while(true){
        //     if(!redisServer.connect()){
        //         System.out.println("Could not Connect to clinet Program Failed.");
        //         break;
        //     }
        //     String msg;
        //     try{
        //         while((msg = redisServer.input.readLine()) != null){
        //             System.out.println("Received: "+ msg);
        //             System.out.println("Responding: " + msg);
        //             redisServer.sendMessage(msg);
        //         }
        //         System.out.println("Client Disconnected.");
        //     }catch(IOException e){
        //         System.out.println("client disconnected abruptly.");
        //     }
        // }
        // redisServer.stop();

        // ServerSocket myRedis;
        try(ServerSocket myRedis = new ServerSocket(6380)){
            System.out.println("Server: Ready and Running on Port: " + 6380);
            while(true){
                System.out.println("Server: waiting for a client.");
                try(Socket client = myRedis.accept()){
                    try(BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        PrintWriter out =  new PrintWriter(client.getOutputStream(),true)){
                            
                        String msg;
                        while((msg = input.readLine()) != null){
                            System.out.println("Server: Received: " + msg);
                            out.println(msg);
                            System.out.println("Server: Sent: " + msg);
                        }
                    }
                }catch( IOException e){
                    System.out.println("Server: client I/O error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server: could not initiate: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Server: Shutting Down.");
    }
}