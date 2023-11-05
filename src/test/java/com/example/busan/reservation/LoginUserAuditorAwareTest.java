package com.example.busan.reservation;

import com.example.busan.auth.AuthController;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Role;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class LoginUserAuditorAwareTest {

    private HttpSession httpSession = new MockHttpSession();
    private LoginUserAuditorAware loginUserAuditorAware = new LoginUserAuditorAware(httpSession);

    @Test
    @DisplayName("현재 로그인한 유저를 얻을 수 있다")
    void getCurrentAuditor() {
        //given
        httpSession.setAttribute(AuthController.AUTHORIZATION, new Authentication("test@gmail.com", Role.USER));

        //when
        final Optional<String> auditor = loginUserAuditorAware.getCurrentAuditor();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(auditor).isPresent();
            softAssertions.assertThat(auditor.get()).isEqualTo("test@gmail.com");
        });
    }
}
