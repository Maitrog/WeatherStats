package com.maitrog.models;

public class ConfigDb {
    public String server;
    public String database;
    public String user;
    public String password;
    public boolean integratedSecurity;
    public boolean encrypt;
    public boolean trustServerCertificate;
    public int loginTimeout;

    @Override
    public String toString() {
        return String.format("jdbc:sqlserver://%s;"
                + "database=%s;"
                + "user=%s;"
                + "password=%s"
                + "integratedSecurity=%b;"
                + "encrypt=%b;"
                + "trustServerCertificate=%b;"
                + "loginTimeout=%d;", server, database, user, password, integratedSecurity, encrypt, trustServerCertificate, loginTimeout);
    }
}
