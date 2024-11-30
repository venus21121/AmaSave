package com.example.retailpricetracker.dtos;

public class LoginUserDto {
    private String email;
    private String password;
    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
