package com.example.busan.auth.dto;

import com.example.busan.member.domain.Member;

import java.util.List;

public record FindEmailResponse(List<String> emails) {

    public static FindEmailResponse from(final List<Member> members) {
        final List<String> emails = members.stream()
                .map(Member::getEmail)
                .toList();

        return new FindEmailResponse(emails);
    }
}
