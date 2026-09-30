package com.miniredis.resp;

public class RespInteger extends Response{

    public RespInteger(long val){
        this.response = ":" + val + "\r\n";
    }

    @Override
    public String getResponse(){
        return this.response;
    }
}