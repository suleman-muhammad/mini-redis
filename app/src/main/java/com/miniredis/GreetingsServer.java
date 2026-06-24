package com.miniredis;
import java.net.*;
import java.io.*;

public class GreetingsServer {
    private ServerSocket serverSocket;
    private Socket clinetSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    public boolean start(int port) throws Exception{
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server is Started waiting for the Clinet to Connect.");
            clinetSocket = serverSocket.accept();
            writer =  new PrintWriter(clinetSocket.getOutputStream(),true);
            reader = new BufferedReader(new InputStreamReader(clinetSocket.getInputStream()));
            String greeting = reader.readLine();
            if("hello server".equals(greeting)){
                writer.println("hello client");
            }else{
                writer.println("Unrecognized request.");
            }
            return true;
        }catch(IOException e){
            return false;
        }
    }

    public void stop() throws Exception{
        writer.close();
        reader.close();
        clinetSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args) throws Exception{
        GreetingsServer server = new GreetingsServer();
        server.start(9999);
    }
}
