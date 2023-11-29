package com.example.busan.member.dto;

import com.example.busan.member.domain.Region;

public record UpdateProfileRequest(String name, String company, Region region) {
}
