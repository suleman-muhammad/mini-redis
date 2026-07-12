package com.miniredis.resp;

public class BulkString extends Response{
    
    public BulkString(String res){
        this.response = "$" + res.length() + "\r\n" + res + "\r\n";
    }
    
    @Override
    public String getResponse(){
        return this.response;
    }
}
