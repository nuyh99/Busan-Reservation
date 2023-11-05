package com.example.busan.member.dto;

public record UpdatePasswordRequest(String email, String phone, String password) {
}
