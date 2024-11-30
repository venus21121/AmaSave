package com.example.retailpricetracker.responses;

public class LoginResponse {
    private String token;

    private long expiresIn;

    // Getters and setters...

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
