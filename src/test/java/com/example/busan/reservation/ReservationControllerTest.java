package com.example.busan.reservation;

import com.example.busan.ApiTest;
import com.example.busan.auth.AuthController;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Role;
import com.example.busan.reservation.domain.Status;
import com.example.busan.reservation.dto.CancelReservationRequest;
import com.example.busan.reservation.dto.CreateReservationRequest;
import com.example.busan.reservation.dto.FindReservationRequest;
import com.example.busan.reservation.dto.ReservationResponse;
import com.example.busan.reservation.dto.UpdateReservationRequest;
import com.example.busan.reservation.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
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

    @Test
    @DisplayName("회의실 예약 수정하기")
    void update() throws Exception {
        //given
        httpSession.setAttribute(AuthController.AUTHORIZATION, new Authentication("test@gmail.com", Role.USER));
        final String request = objectMapper.writeValueAsString(new UpdateReservationRequest(
                1L, LocalDateTime.of(2023, 11, 10, 14, 0), LocalDateTime.of(2023, 11, 10, 15, 0)));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/reservations/{reservationId}", 1L)
                                .session(httpSession)
                                .content(request)
                                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andDo(document("자신의 회의실 예약 수정하기",
                        pathParameters(parameterWithName("reservationId").description("수정할 예약 ID")),
                        requestFields(
                                fieldWithPath("roomId").description("예약할 회의실 ID"),
                                fieldWithPath("startTime").description("시작 시각"),
                                fieldWithPath("endTime").description("종료 시각"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("자신의 회의실 예약 목록 조회하기")
    void findAllByCurrentLoggedInMember() throws Exception {
        //given
        httpSession.setAttribute(AuthController.AUTHORIZATION, new Authentication("test@gmail.com", Role.USER));

        final ReservationResponse reservation1 = new ReservationResponse(
                1L, Status.RESERVED, null, LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                "황재현", "01012341234", LocalDateTime.now(), 1L, "대회의실", "요기요");
        final ReservationResponse reservation2 = new ReservationResponse(
                2L, Status.CANCELED, "쓰기 싫어졌어요..", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                "황재현", "01012341234", LocalDateTime.now(), 1L, "대회의실", "토스뱅크");

        given(reservationService.findAllByCurrentLoggedInMember(any(), any()))
                .willReturn(new PageImpl<>(List.of(reservation1, reservation2)));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/reservations")
                                .queryParam("page", "1")
                                .queryParam("size", "10")
                                .session(httpSession))
                .andDo(print())
                .andDo(document("자신의 회의실 예약 목록 최신 순으로 보기",
                        queryParameters(
                                parameterWithName("page").description("페이지는 1부터 시작 (디폴트값 1)").optional(),
                                parameterWithName("size").description("페이지별 사이즈 (디폴트값 10)").optional()),
                        responseFields(
                                fieldWithPath("content.[].id").description("예약 ID"),
                                fieldWithPath("content.[].status").description("예약 상태"),
                                fieldWithPath("content.[].cancelReason").description("취소 상태일 경우 취소 이유(취소가 아니면 null)").optional(),
                                fieldWithPath("content.[].startTime").description("시작 시각"),
                                fieldWithPath("content.[].endTime").description("종료 시각"),
                                fieldWithPath("content.[].name").description("예약자 성함"),
                                fieldWithPath("content.[].phone").description("예약자 휴대폰 번호"),
                                fieldWithPath("content.[].reservedAt").description("예약 일시"),
                                fieldWithPath("content.[].roomId").description("회의실 ID"),
                                fieldWithPath("content.[].company").description("예약자 회사"),
                                fieldWithPath("content.[].roomName").description("회의실 이름"),
                                fieldWithPath("last").description("마지막 페이지인지"),
                                fieldWithPath("first").description("첫 페이지인지"),
                                fieldWithPath("empty").description("빈 데이터인지"),
                                fieldWithPath("totalPages").description("전체 페이지 개수"),
                                fieldWithPath("totalElements").description("전체 DB 데이터 요소 개수"),
                                fieldWithPath("numberOfElements").description("현재 응답 데이터 요소 개수"),
                                fieldWithPath("size").description("한 페이지당 사이즈"),
                                fieldWithPath("number").description("현재 페이지"),
                                fieldWithPath("pageable").description("요청한 페이지네이션 정보").ignored(),
                                fieldWithPath("sort.*").description("정렬 정보").ignored())))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("전체 회의실 예약 목록 조회하기")
    void findAll() throws Exception {
        //given
        final ReservationResponse reservation1 = new ReservationResponse(
                1L, Status.RESERVED, null, LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                "황재현", "01012341234", LocalDateTime.now(), 1L, "대회의실", "요기요");
        final ReservationResponse reservation2 = new ReservationResponse(
                2L, Status.CANCELED, "쓰기 싫어졌어요..", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                "황재현", "01012341234", LocalDateTime.now(), 1L, "대회의실", "토스뱅크");
        final String request = objectMapper.writeValueAsString(
                new FindReservationRequest(LocalDateTime.now(), LocalDateTime.now()));

        given(reservationService.findAll(any(), any()))
                .willReturn(new PageImpl<>(List.of(reservation1, reservation2)));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/reservations/all")
                                .queryParam("page", "1")
                                .queryParam("size", "10")
                                .queryParam("start", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                                .queryParam("end", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                                .session(httpSession))
                .andDo(print())
                .andDo(document("전체 회의실 예약 목록 최신 순으로 보기",
                        queryParameters(
                                parameterWithName("page").description("페이지는 1부터 시작 (디폴트값 1)").optional(),
                                parameterWithName("size").description("페이지별 사이즈 (디폴트값 10)").optional(),
                                parameterWithName("start").description("시작 시각(포함)"),
                                parameterWithName("end").description("종료 시각(포함)")),
                        responseFields(
                                fieldWithPath("content.[].id").description("예약 ID"),
                                fieldWithPath("content.[].status").description("예약 상태"),
                                fieldWithPath("content.[].cancelReason").description("취소 상태일 경우 취소 이유(취소가 아니면 null)").optional(),
                                fieldWithPath("content.[].startTime").description("시작 시각"),
                                fieldWithPath("content.[].endTime").description("종료 시각"),
                                fieldWithPath("content.[].name").description("예약자 성함"),
                                fieldWithPath("content.[].phone").description("예약자 휴대폰 번호"),
                                fieldWithPath("content.[].reservedAt").description("예약 일시"),
                                fieldWithPath("content.[].roomId").description("회의실 ID"),
                                fieldWithPath("content.[].company").description("예약자 회사"),
                                fieldWithPath("content.[].roomName").description("회의실 이름"),
                                fieldWithPath("last").description("마지막 페이지인지"),
                                fieldWithPath("first").description("첫 페이지인지"),
                                fieldWithPath("empty").description("빈 데이터인지"),
                                fieldWithPath("totalPages").description("전체 페이지 개수"),
                                fieldWithPath("totalElements").description("전체 DB 데이터 요소 개수"),
                                fieldWithPath("numberOfElements").description("현재 응답 데이터 요소 개수"),
                                fieldWithPath("size").description("한 페이지당 사이즈"),
                                fieldWithPath("number").description("현재 페이지"),
                                fieldWithPath("pageable").description("요청한 페이지네이션 정보").ignored(),
                                fieldWithPath("sort.*").description("정렬 정보").ignored())))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
