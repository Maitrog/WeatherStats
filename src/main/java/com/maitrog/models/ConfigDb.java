package com.maitrog.models;

public class ConfigDb {
    public String server;
    public String database;
    public String user;
    public String password;

    @Override
    public String toString() {
        return String.format("jdbc:sqlserver://%s;"
                + "database=%s;", server, database);
    }
}
