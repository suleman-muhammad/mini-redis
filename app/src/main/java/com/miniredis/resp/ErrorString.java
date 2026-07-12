package com.miniredis.resp;

public class ErrorString extends Response{
    
    public ErrorString(String res){
        this.response = "-" + res + "\r\n";
    }

    @Override
    public String getResponse(){
        return this.response;
    }
}
