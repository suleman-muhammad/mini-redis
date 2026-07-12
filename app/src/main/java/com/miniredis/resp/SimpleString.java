package com.miniredis.resp;

public class SimpleString extends Response{

    public SimpleString(String res){
        this.response = "+" + res + "\r\n";
    }

    @Override
    public String getResponse(){
        return this.response;
    }
}
