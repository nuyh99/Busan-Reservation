package com.example.busan.auth.dto;

import com.example.busan.member.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticationTest {

    @Test
    @DisplayName("관리자가 아니면 예외가 발생한다")
    void validateAdmin() {
        //given
        final Authentication authentication = new Authentication("hi@naver.com", Role.USER);

        //when, then
        assertThatThrownBy(() -> authentication.validateAdmin())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
