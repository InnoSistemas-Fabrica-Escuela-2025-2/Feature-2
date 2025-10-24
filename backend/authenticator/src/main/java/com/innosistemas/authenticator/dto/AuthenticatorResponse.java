package com.innosistemas.authenticator.dto;

public class AuthenticatorResponse {
    private String token;
    private String email;
 
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}

