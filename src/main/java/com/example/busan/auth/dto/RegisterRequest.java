package com.example.busan.auth.dto;

import com.example.busan.member.domain.Region;

public record RegisterRequest(String id, String password, Region region, String company) {
}
