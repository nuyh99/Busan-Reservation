package com.example.busan.auth.domain;

public record Auth(String id, Role role) {

    public static Auth from(final Member member) {
        return new Auth(member.getId(), member.getRole());
    }
}
