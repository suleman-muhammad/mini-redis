package com.miniredis.resp;

public class RespInteger extends Response{

    public RespInteger(int val){
        this.response = ":" + val + "\r\n";
    }

    @Override
    public String getResponse(){
        return this.response;
    }
}