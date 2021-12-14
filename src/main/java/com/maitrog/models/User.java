package com.maitrog.models;

public class User {
    private String login;
    private String passwordHash;
    private String language;
    private Role role;

    public User(String login, String passwordHash, String language, Role role) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.language = language;
        this.role = role;
    }

    public User(String login, String passwordHash, Role role) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User() {
    }


    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getLanguage() { return language; }

    public Role getRole() { return role; }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setLanguage(String language) { this.language = language; }

    public void setRole(Role role) { this.role = role; }
}
