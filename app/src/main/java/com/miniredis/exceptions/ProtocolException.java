package com.miniredis.exceptions;

import java.io.IOException;

public class ProtocolException extends IOException{
    ProtocolException(String message){
        super(message);
    }
}
