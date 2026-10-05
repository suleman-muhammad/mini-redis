package com.miniredis.persistence;

import java.util.Map;
import java.util.HashMap;


public class Optimizer {
    private final String FILE_PATH = "data/commands.txt";

    private Map<String,String> logs;

    public Optimizer(){
        this.logs = new HashMap<>();
    }

}
