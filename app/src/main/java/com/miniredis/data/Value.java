package com.miniredis.data;

public record Value(String val, long expiresAtMillis) {
    
    public boolean isExpired(){
        return expiresAtMillis != 0 && System.currentTimeMillis() >= expiresAtMillis;
    }
}
