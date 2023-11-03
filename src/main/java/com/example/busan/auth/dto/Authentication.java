package com.example.busan.auth.dto;

import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.Role;

public record Authentication(String id, Role role) {

    public static Authentication from(final Member member) {
        return new Authentication(member.getId(), member.getRole());
    }
}
