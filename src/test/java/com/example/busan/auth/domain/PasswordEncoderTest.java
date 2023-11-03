package com.example.busan.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PasswordEncoderTest {

    private PasswordEncoder passwordEncoder = new PasswordEncoder();

    @ParameterizedTest
    @DisplayName("비밀번호를 해싱할 수 있다")
    @ValueSource(strings = {"test", "password", "hihihsilfajeilj", "wlqrkrhtlvek12321321"})
    void encode(final String password) {
        //when
        final String encoded = passwordEncoder.encode(password);

        //then
        assertThat(encoded).isNotEqualTo(password);
    }

    @Test
    @DisplayName("비밀번호가 같은 지 확인할 수 있다")
    void validate() {
        //given
        final String password = "연어";
        final String encoded = passwordEncoder.encode(password);

        //when, then
        assertDoesNotThrow(() -> passwordEncoder.validateEquals(password, encoded));
    }
}
