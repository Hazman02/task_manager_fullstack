package com.assignment.backend.dto;

public class AuthResponse {
    public String token;

    public AuthResponse() {}

    public AuthResponse(String token) {
        this.token = token;
    }
}
