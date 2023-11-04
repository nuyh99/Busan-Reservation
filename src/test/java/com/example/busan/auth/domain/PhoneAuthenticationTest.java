package com.example.busan.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneAuthenticationTest {

    @Test
    @DisplayName("만료됐는지 검증할 수 있다")
    void isExpired() {
        //given
        final PhoneAuthentication authentication = new PhoneAuthentication("012041", LocalDateTime.now());

        //when
        final boolean expired = authentication.isExpired();

        //then
        assertThat(expired).isTrue();
    }
}
