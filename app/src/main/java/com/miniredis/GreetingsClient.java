package com.miniredis;

import java.net.*;
import java.io.*;

public class GreetingsClient {
    
    private Socket clinetSocket;
    private PrintWriter writer;
    private BufferedReader reader;



    public void startConnection(String ip, int port) throws Exception{
        clinetSocket = new Socket(ip,port);
        writer = new PrintWriter(clinetSocket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(clinetSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws Exception{
        writer.println(msg);
        String response = reader.readLine();
        return response;
    }

    public void StopConnection() throws Exception{
        writer.close();
        reader.close();
        clinetSocket.close();
    }
}
