package com.example.busan.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByEmailAndPhone(String email, String phone);
}
