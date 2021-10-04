package com.himmash.database;

public class ConnectionBuilderFactory {
    public static ConnectionBuilder getConnectionBuilder(){
        return new SimpleConnectionBuilder();
    }
}
