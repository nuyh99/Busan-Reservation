package com.example.busan.room;

import com.example.busan.ApiTest;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Role;
import com.example.busan.room.dto.CreateRoomRequest;
import com.example.busan.room.dto.ReservationResponse;
import com.example.busan.room.dto.RoomResponse;
import com.example.busan.room.dto.UpdateRoomRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.example.busan.auth.AuthController.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class RoomControllerTest extends ApiTest {

    private final MockHttpSession httpSession = new MockHttpSession();
    @MockBean
    private RoomService roomService;

    @BeforeEach
    void clearSession() {
        httpSession.clearAttributes();
    }

    @Test
    @DisplayName("회의실 생성 하기")
    void create() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("test@gmail.com", Role.ADMIN));
        final String request = objectMapper.writeValueAsString(new CreateRoomRequest("컨퍼런스룸 1", "https://image.com", 14));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/rooms")
                                .session(httpSession)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회의실 생성 - 어드민",
                        requestFields(
                                fieldWithPath("name").description("회의실 이름"),
                                fieldWithPath("image").description("회의실 이미지 url"),
                                fieldWithPath("maxPeopleCount").description("회의실 최대 수용 인원"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회의실 삭제 하기")
    void deleteById() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("test@gmail.com", Role.ADMIN));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/rooms/{roomId}", 1)
                                .session(httpSession))
                .andDo(print())
                .andDo(document("회의실 삭제 - 어드민",
                        pathParameters(
                                parameterWithName("roomId").description("회의실 ID"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("회의실 수정 하기")
    void update() throws Exception {
        //given
        httpSession.setAttribute(AUTHORIZATION, new Authentication("test@gmail.com", Role.ADMIN));
        final String request = objectMapper.writeValueAsString(new UpdateRoomRequest("updated", "updated", 10));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/rooms/{roomId}", 1)
                                .session(httpSession)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("회의실 수정 - 어드민",
                        requestFields(
                                fieldWithPath("name").description("회의실 이름"),
                                fieldWithPath("image").description("회의실 이미지 url"),
                                fieldWithPath("maxPeopleCount").description("회의실 최대 수용 인원"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("회의실 전체 조회하기")
    void findAll() throws Exception {
        //given
        final ReservationResponse reservation1 = new ReservationResponse(1L, LocalTime.of(13, 0), LocalTime.of(16, 0), true);
        final ReservationResponse reservation2 = new ReservationResponse(2L, LocalTime.of(16, 0), LocalTime.of(17, 0), false);
        final RoomResponse roomResponse = new RoomResponse(1L, "대회의실", "image.com", 10, List.of(reservation1, reservation2));
        given(roomService.findAllAtDate(any(), any()))
                .willReturn(List.of(roomResponse));

        //when
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/rooms").queryParam("date", "2023-03-19"))
                .andDo(print())
                .andDo(document("회의실 전체 조회하기",
                        queryParameters(parameterWithName("date").description("조회할 날짜 (없으면 당일 날짜로 조회됨)").optional()),
                        responseFields(
                                fieldWithPath("[].roomId").description("회의실 ID"),
                                fieldWithPath("[].name").description("회의실 이름"),
                                fieldWithPath("[].image").description("회의실 이미지 url"),
                                fieldWithPath("[].maxPeopleCount").description("최대 수용 인원 수"),
                                fieldWithPath("[].reservations.[].reservationId").description("예약 ID"),
                                fieldWithPath("[].reservations.[].startTime").description("예약 시작 시각"),
                                fieldWithPath("[].reservations.[].endTime").description("예약 종료 시각"),
                                fieldWithPath("[].reservations.[].isMine").description("현재 로그인한 사람의 것인지 여부"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
