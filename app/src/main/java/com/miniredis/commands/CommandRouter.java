package com.miniredis.commands;

import com.miniredis.data.Store;

public class CommandRouter {
    
    private final Store store;

    public CommandRouter(Store store){
        this.store = store;
    }

    
}
