package com.miniredis.resp;

public class NullString extends Response{
    
    public static NullString instance = new NullString();
    
    private NullString(){
        this.response = "$-1\r\n";
    }

    @Override
    public String getResponse(){
        return this.response;
    }
}
