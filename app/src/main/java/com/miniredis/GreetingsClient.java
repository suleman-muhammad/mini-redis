package com.miniredis;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class GreetingsClient {
    
    private Socket clinetSocket;
    private OutputStream writer;
    private BufferedReader reader;



    public void startConnection(String ip, int port) throws Exception{
        clinetSocket = new Socket(ip,port);
        writer = clinetSocket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(clinetSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws Exception{
        writer.write(msg.getBytes(StandardCharsets.UTF_8));
        writer.flush();
        String response = reader.readLine();
        return response;
    }

    public void StopConnection() throws Exception{
        writer.close();
        reader.close();
        clinetSocket.close();
    }



    public static void main(String[] args) {

        // String[] msgs = {"Hello","How are you..?", "Can you hear us..?","is Anyone there..?"};
        String[] msgs = {"$4\r\nPING\r\n","*3\r\n$3\r\nSET\r\n$7\r\nChauhan\r\n$7\r\nSuleman\r\n","*2\r\n$3\r\nGET\r\n$7\r\nChauhan\r\n"};
        GreetingsClient client = new GreetingsClient();

        try{
            client.startConnection("127.0.0.1",6380);
            
        }catch (Exception e){
            System.out.println("Failed.");
            return;
        }

        for (String msg: msgs){  
            try{
                System.out.println("Sending: " + msg);
                String response = client.sendMessage(msg);
                System.out.println("Response: " + response);
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
                return;
            }catch (IOException e){
                e.printStackTrace();
                return;
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        try{
            client.StopConnection();
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
}
