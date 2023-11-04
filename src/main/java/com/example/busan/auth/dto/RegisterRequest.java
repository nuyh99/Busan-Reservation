package com.example.busan.auth.dto;

import com.example.busan.member.domain.Region;

public record RegisterRequest(String email, String password, Region region, String company) {
}
