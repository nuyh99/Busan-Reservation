package com.example.busan.member;

import com.example.busan.ApiTest;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.auth.service.PhoneAuthenticator;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.Region;
import com.example.busan.member.domain.Role;
import com.example.busan.member.dto.EmailDuplicateResponse;
import com.example.busan.member.dto.MemberInfoResponse;
import com.example.busan.member.dto.UpdatePasswordRequest;
import com.example.busan.member.dto.UpdatePhoneRequest;
import com.example.busan.member.dto.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.example.busan.auth.AuthController.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class MemberControllerTest extends ApiTest {

    private final MockHttpSession httpSession = new MockHttpSession();
    @MockBean
    private MemberService memberService;
    @MockBean
    private PhoneAuthenticator phoneAuthenticator;

    @BeforeEach
    void clearSession() {
        httpSession.clearAttributes();
    }

    @Test
    @DisplayName("회원 가입 하기")
    void register() throws Exception {
        //given
        final String request = objectMapper.writeValueAsString(
                new RegisterRequest("email@naver.com", "password", "name", "0101234", Region.GANGNEUNG, "company"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/members")
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회원 가입 하기",
                        requestFields(
                                fieldWithPath("email").description("이메일 형식"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("phone").description("인증 완료된 휴대폰 번호"),
                                fieldWithPath("password").description(String.format("특수문자 1개 이상, 영문과 숫자 조합 %d ~ %d", Member.PASSWORD_MINIMUM_LENGTH, Member.PASSWORD_MAXIMUM_LENGTH)),
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
                        delete("/members").session(httpSession))
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

    @Test
    @DisplayName("이메일 중복 확인하기")
    void isDuplicatedEmail() throws Exception {
        //given
        given(memberService.isDuplicated("test@gmail.com"))
                .willReturn(new EmailDuplicateResponse(false));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/members/{email}", "test@gmail.com"))
                .andDo(print())
                .andDo(document("이메일 중복 확인하기",
                        pathParameters(parameterWithName("email").description("확인할 이메일")),
                        responseFields(fieldWithPath("isDuplicated").description("중복 여부"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 정보 수정하기 - 이름, 회사명, 이메일")
    void changeProfile() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("test@gmail.com", Role.ADMIN));
        final String request = objectMapper.writeValueAsString(
                new UpdateProfileRequest("name", "company", "test@naver.com"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/members/profile").session(httpSession)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회원 정보 수정하기 - 이름, 회사명, 이메일",
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("company").description("회사명"),
                                fieldWithPath("email").description("이메일"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("회원 휴대폰 번호 변경하기")
    void changePhone() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("test@gmail.com", Role.ADMIN));
        final String request = objectMapper.writeValueAsString(new UpdatePhoneRequest("01012341234"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/members/phone").session(httpSession)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회원 휴대폰 번호 변경하기",
                        requestFields(fieldWithPath("phone").description("인증 완료된 휴대폰 번호"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("회원 비밀번호 변경하기")
    void changePassword() throws Exception {
        //given
        final String request = objectMapper.writeValueAsString(
                new UpdatePasswordRequest("test@naver.com", "01012341234", "@@newPassword123"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/members/password")
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("비밀번호 분실 시 변경하기",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("phone").description("인증 완료된 휴대폰 번호"),
                                fieldWithPath("password").description("새 비밀번호"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("현재 로그인 유저 정보 얻기")
    void getMemberInfo() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("email@naver.com", Role.USER));
        given(memberService.getMemberInfo(any()))
                .willReturn(new MemberInfoResponse("연어", "01012341234", "test@naver.com", Role.USER, "우형", LocalDateTime.now()));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/members").session(httpSession))
                .andDo(print())
                .andDo(document("현재 유저 정보 조회하기",
                        responseFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("phone").description("휴대폰"),
                                fieldWithPath("company").description("회사"),
                                fieldWithPath("createdAt").description("가입일"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("role").description("계정 권한"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
