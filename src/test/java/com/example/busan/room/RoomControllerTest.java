package com.example.busan.room;

import com.example.busan.ApiTest;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Role;
import com.example.busan.room.dto.CreateRoomRequest;
import com.example.busan.room.dto.UpdateRoomRequest;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
}
