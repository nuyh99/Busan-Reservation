package com.example.busan.member.domain;

import com.example.busan.auth.domain.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("휴대폰으로 이메일 찾기")
    void findByPhone() {
        //given
        final Member member = createMember();

        //when
        final Optional<Member> found = memberRepository.findByPhone(member.getPhone());

        //then
        assertThat(found).isPresent();
    }

    private Member createMember() {
        final Member member = new Member("test@naver.com", "name", "@password1234", Region.GANGNEUNG, "01012345678", "부경대", new PasswordEncoder());
        return memberRepository.save(member);
    }
}
