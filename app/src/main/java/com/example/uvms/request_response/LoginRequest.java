package com.example.uvms.request_response;

public class LoginRequest {
    private String email;
    private String password;

    // Constructors, getters, and setters
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
