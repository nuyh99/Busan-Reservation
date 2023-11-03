package com.example.busan.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "         ", "tests  ", "dslfjaskldfjklsafjlkasjfkleajsklefjsaklfjkl"})
    @DisplayName("아이디 검증")
    void validateId(final String invalidId) {
        assertThatThrownBy(() -> new Member(invalidId, "password", Region.EMPTY, "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "         ", "testsfd  ", "dslfjaskldfjklsafjlkasjfkleajsklefjsaklfjkl"})
    @DisplayName("비밀번호 검증")
    void validatePassword(final String invalidPassword) {
        assertThatThrownBy(() -> new Member("idididid", invalidPassword, Region.EMPTY, "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지역이 없으면 예외가 발생한다")
    void validateRegion() {
        assertThatThrownBy(() -> new Member("idididid", "passwordpasswo", null, "company", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회사가 없으면 예외가 발생한다")
    void validateCompany() {
        assertThatThrownBy(() -> new Member("idididid", "passwordpasswo", Region.EMPTY, null, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비밀번호가 다르면 예외가 발생한다")
    void checkPassword() {
        //given
        final Member member = new Member("ididididid", "password1234", Region.EMPTY, "company", passwordEncoder);

        //when, then
        assertThatThrownBy(() -> member.checkPassword("password12345", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
