package com.example.busan.auth.dto;

public record LoginRequest(String email, String password, boolean isAuto) {
}
