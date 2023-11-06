package com.example.busan.reservation;

import com.example.busan.ApiTest;
import com.example.busan.auth.AuthController;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Role;
import com.example.busan.reservation.dto.CancelReservationRequest;
import com.example.busan.reservation.dto.CreateReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class ReservationControllerTest extends ApiTest {

    private final MockHttpSession httpSession = new MockHttpSession();
    @MockBean
    private ReservationService reservationService;

    @Test
    @DisplayName("회의실 예약하기")
    void reserve() throws Exception {
        //given
        httpSession.setAttribute(AuthController.AUTHORIZATION, new Authentication("test@gmail.com", Role.USER));

        final String request = objectMapper.writeValueAsString(new CreateReservationRequest(
                1L, LocalDateTime.of(2023, 11, 10, 14, 0), LocalDateTime.of(2023, 11, 10, 15, 0)));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/reservations")
                                .session(httpSession)
                                .content(request)
                                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회의실 예약하기",
                        requestFields(
                                fieldWithPath("roomId").description("예약할 회의실 ID"),
                                fieldWithPath("startTime").description("시작 시각"),
                                fieldWithPath("endTime").description("종료 시각"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회의실 예약 취소하기")
    void cancel() throws Exception {
        //given
        httpSession.setAttribute(AuthController.AUTHORIZATION, new Authentication("test@gmail.com", Role.USER));
        final String request = objectMapper.writeValueAsString(new CancelReservationRequest("쓰기 싫어요"));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/reservations/{reservationId}", 1L)
                                .session(httpSession)
                                .content(request)
                                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andDo(document("자신의 회의실 예약 취소하기",
                        pathParameters(parameterWithName("reservationId").description("취소할 예약 ID")),
                        requestFields(fieldWithPath("reason").description("취소 사유"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
