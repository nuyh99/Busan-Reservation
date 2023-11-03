package com.example.busan.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "         ", "tests  ", "dslfjaskldfjklsafjlkasjfkleajsklefjsaklfjkl"})
    @DisplayName("아이디 검증")
    void validateId(final String invalidId) {
        assertThatThrownBy(() -> new Member(invalidId, "password", Region.EMPTY, "company"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "         ", "testsfd  ", "dslfjaskldfjklsafjlkasjfkleajsklefjsaklfjkl"})
    @DisplayName("비밀번호 검증")
    void validatePassword(final String invalidPassword) {
        assertThatThrownBy(() -> new Member("idididid", invalidPassword, Region.EMPTY, "company"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지역이 없으면 예외가 발생한다")
    void validateRegion() {
        assertThatThrownBy(() -> new Member("idididid", "passwordpasswo", null, "company"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회사가 없으면 예외가 발생한다")
    void validateCompany() {
        assertThatThrownBy(() -> new Member("idididid", "passwordpasswo", Region.EMPTY, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
