package com.miniredis;
import java.net.*;

import javax.tools.StandardJavaFileManager;

import java.io.*;
class MiniRedisServer {
    private Socket curClient;
    private ServerSocket redisServer;
    private PrintWriter output;
    private BufferedReader input;


    public boolean start(){
        try{
            redisServer = new ServerSocket(9999);
            curClient = null;
            output = new PrintWriter(curClient.getOutputStream(),true);
            input = new BufferedReader(new InputStreamReader(curClient.getInputStream()));
            return true;
            
        }catch (Exception e){
            System.out.println("Cannot Start Server.");
            return false;
        }
    }
}