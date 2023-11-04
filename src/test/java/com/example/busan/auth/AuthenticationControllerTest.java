package com.example.busan.auth;

import com.example.busan.ApiTest;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.LoginRequest;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.auth.service.AuthService;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.Region;
import com.example.busan.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.example.busan.auth.AuthController.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class AuthenticationControllerTest extends ApiTest {

    @MockBean
    private AuthService authService;
    private final MockHttpSession httpSession = new MockHttpSession();

    @BeforeEach
    void clearSession() {
        httpSession.clearAttributes();
    }

    @Test
    @DisplayName("로그인 하기")
    void login() throws Exception {
        //given
        final Authentication authentication = new Authentication("email@naver.com", Role.USER);
        given(authService.login(any()))
                .willReturn(authentication);
        final String request = objectMapper.writeValueAsString(new LoginRequest("email@naver.com", "password"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/auth/login")
                                .session(httpSession)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("로그인 하기",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"))))
                .andReturn()
                .getResponse();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
            softAssertions.assertThat(httpSession.getAttribute(AUTHORIZATION)).isEqualTo(authentication);
        });
    }

    @Test
    @DisplayName("로그아웃 하기")
    void logout() throws Exception {
        final Authentication authentication = new Authentication("email@naver.com", Role.USER);
        httpSession.setAttribute(AUTHORIZATION, authentication);

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/auth/logout").session(httpSession))
                .andDo(print())
                .andDo(document("로그아웃 하기"))
                .andReturn()
                .getResponse();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
            softAssertions.assertThat(httpSession.getAttribute(AUTHORIZATION)).isNull();
        });
    }

    @Test
    @DisplayName("회원 가입 하기")
    void register() throws Exception {
        //given
        final String request = objectMapper.writeValueAsString(
                new RegisterRequest("email@naver.com", "password", "name", Region.GANGNEUNG, "company"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/auth/register")
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회원 가입 하기",
                        requestFields(
                                fieldWithPath("email").description("이메일 형식"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("password").description("비밀번호, 공백 제외 " + Member.PASSWORD_MINIMUM_LENGTH + " ~ " + Member.PASSWORD_MAXIMUM_LENGTH),
                                fieldWithPath("region").description("지역"),
                                fieldWithPath("company").description("회사"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회원 탈퇴 하기")
    void withdraw() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("email@naver.com", Role.USER));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/auth").session(httpSession))
                .andDo(print())
                .andDo(document("회원 탈퇴 하기"))
                .andReturn()
                .getResponse();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
            softAssertions.assertThat(httpSession.getAttribute(AUTHORIZATION)).isNull();
        });
    }
}
