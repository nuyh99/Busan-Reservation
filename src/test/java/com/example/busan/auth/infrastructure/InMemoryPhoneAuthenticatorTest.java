package com.example.busan.auth.infrastructure;

import com.example.busan.auth.domain.AuthorizationCodeGenerator;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

@SpringBootTest
class InMemoryPhoneAuthenticatorTest {

    @Autowired
    private InMemoryPhoneAuthenticator phoneAuthenticator;
    @MockBean
    private DefaultMessageService messageService;
    @MockBean
    private AuthorizationCodeGenerator codeGenerator;

    @Test
    @DisplayName("휴대폰 인증 코드를 전송할 수 있다")
    void sendCode() {
        //given
        given(codeGenerator.generate())
                .willReturn("123455");

        final String phone = "01012345";

        //when
        phoneAuthenticator.sendCode(phone);

        //then
        verify(messageService, times(1)).sendOne(any());
    }

    @Test
    @DisplayName("인증 코드로 인증을 완료할 수 있다")
    void authenticate() {
        //given
        given(codeGenerator.generate())
                .willReturn("192473");

        final String phone = "01012345";
        phoneAuthenticator.sendCode(phone);

        //when
        phoneAuthenticator.authenticate("192473");

        //then
        assertDoesNotThrow(() -> phoneAuthenticator.validateAuthenticated(phone));
    }

    @Test
    @DisplayName("잘못된 인증 코드로 인증하면 예외가 발생한다")
    void authenticate_fail() {
        assertThatThrownBy(() -> phoneAuthenticator.authenticate("192473"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 인증 코드입니다.");
    }
}
