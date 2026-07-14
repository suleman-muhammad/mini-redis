package com.miniredis.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.miniredis.commands.CommandRouter;
import com.miniredis.resp.RespReader;
import com.miniredis.resp.RespWriter;
import com.miniredis.resp.Response;

public class ClientHandler {
    private final Socket client;
    private final RespReader reader;
    private final RespWriter writer;
    private final CommandRouter cr;

    public ClientHandler(Socket client,CommandRouter cr) throws IOException{
        this.client = client;
        this.reader = new RespReader(client.getInputStream());
        this.writer = new RespWriter(client.getOutputStream());
        this.cr = cr;
    }
    
    public void handleClient(){
        System.out.println("Server: Connected a new Client.");
        try{
            List<String> commands;
            while((commands = reader.readCommand()) != null){
                Response res = cr.handle(commands);
                writer.write(res);
            }
            System.out.println("Server: Client Disconnected gracefully.");
        }
        catch (IOException e){
            System.out.println("Server: Client Disconnected abruptly: " + e.getMessage());
        }
    }
}
