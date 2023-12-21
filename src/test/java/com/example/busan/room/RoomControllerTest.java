package com.example.busan.room;

import com.example.busan.ApiTest;
import com.example.busan.reservation.domain.Status;
import com.example.busan.room.dto.ReservationResponse;
import com.example.busan.room.dto.RoomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class RoomControllerTest extends ApiTest {

    private final MockHttpSession httpSession = new MockHttpSession();
    @MockBean
    private RoomService roomService;

    @Test
    @DisplayName("회의실 전체 조회하기")
    void findAll() throws Exception {
        //given
        final ReservationResponse reservation1 = new ReservationResponse(1L, LocalTime.of(13, 0), LocalTime.of(16, 0), true, Status.CANCELED);
        final ReservationResponse reservation2 = new ReservationResponse(2L, LocalTime.of(16, 0), LocalTime.of(17, 0), false, Status.RESERVED);
        final RoomResponse roomResponse = new RoomResponse(1L, "대회의실", "image.com", 10, List.of(reservation1, reservation2), 3);
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
                                fieldWithPath("[].sequence").description("회의실 순서"),
                                fieldWithPath("[].maxPeopleCount").description("최대 수용 인원 수"),
                                fieldWithPath("[].reservations.[].reservationId").description("예약 ID"),
                                fieldWithPath("[].reservations.[].startTime").description("예약 시작 시각"),
                                fieldWithPath("[].reservations.[].endTime").description("예약 종료 시각"),
                                fieldWithPath("[].reservations.[].isMine").description("현재 로그인한 사람의 것인지 여부"),
                                fieldWithPath("[].reservations.[].status").description("취소 여부"))))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
