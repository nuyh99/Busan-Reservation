package com.example.busan.auth.dto;

import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.Role;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record Authentication(String email, @Enumerated(value = EnumType.STRING) Role role) {

    public static Authentication from(final Member member) {
        return new Authentication(member.getEmail(), member.getRole());
    }

    public void validateAdmin() {
        if (role != Role.ADMIN) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }
    }
}
