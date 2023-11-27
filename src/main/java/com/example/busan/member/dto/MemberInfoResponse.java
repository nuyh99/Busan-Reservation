package com.example.busan.member.dto;

import com.example.busan.member.domain.Region;
import com.example.busan.member.domain.Role;

import java.time.LocalDateTime;

public record MemberInfoResponse(String name, String phone, String email, Role role, String company,
                                 Region region, LocalDateTime createdAt) {
}
