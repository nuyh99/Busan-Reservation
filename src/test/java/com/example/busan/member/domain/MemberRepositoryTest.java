package com.example.busan.member.domain;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.domain.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.truncate();
    }

    @Test
    @DisplayName("멤버 저장 시 시간도 저장된다")
    void save() {
        //given
        final Member member = new Member("test@test.com", "salmon", "@@pasSword1", Region.BUSAN, "01012341234", "company", passwordEncoder);

        //when
        final Member saved = memberRepository.save(member);

        //then
        assertThat(saved.getCreatedAt()).isNotNull();
    }

}
