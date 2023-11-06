package com.example.busan.room;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Role;
import com.example.busan.reservation.LoginUserAuditorAware;
import com.example.busan.reservation.domain.Reservation;
import com.example.busan.reservation.domain.ReservationRepository;
import com.example.busan.room.domain.Room;
import com.example.busan.room.domain.RoomRepository;
import com.example.busan.room.dto.CreateRoomRequest;
import com.example.busan.room.dto.ReservationResponse;
import com.example.busan.room.dto.RoomResponse;
import com.example.busan.room.dto.UpdateRoomRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class RoomServiceTest {

    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @MockBean
    private LoginUserAuditorAware auditorAware;
    @Autowired
    private DatabaseCleaner cleaner;

    @BeforeEach
    void cleanUp() {
        given(auditorAware.getCurrentAuditor())
                .willReturn(Optional.of("test@naver.com"));
        cleaner.truncate();
    }

    @Test
    @DisplayName("회의실 저장")
    void save() {
        //given
        final CreateRoomRequest request = new CreateRoomRequest("대회의실", "image.com", 15);

        //when
        roomService.save(request);

        //then
        assertThat(roomRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("회의실 삭제")
    void deleteById() {
        //given
        final Room room = createRoom();

        //when
        roomService.deleteById(room.getId());

        //then
        assertThat(roomRepository.findById(room.getId())).isEmpty();
    }

    @Test
    @DisplayName("회의실 업데이트")
    void update() {
        //given
        final Room room = createRoom();
        final UpdateRoomRequest request = new UpdateRoomRequest("updated", "newImage", 100);

        //when
        roomService.update(room.getId(), request);

        //then
        final Room updated = roomRepository.findById(room.getId()).get();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(updated.getName()).isEqualTo("updated");
            softAssertions.assertThat(updated.getImage()).isEqualTo("newImage");
            softAssertions.assertThat(updated.getMaxPeopleCount()).isEqualTo(100);
        });
    }

    @Test
    @DisplayName("회의실 전체 조회 - 현재 로그인한 유저가 모두 예약했을 때")
    void findAll() {
        //given
        final Authentication authentication = new Authentication("test@naver.com", Role.USER);

        final Room room = createRoom();
        final Reservation reservation1 = new Reservation(room.getId(),
                LocalDateTime.of(2023, 11, 10, 13, 0), LocalDateTime.of(2023, 11, 10, 15, 30));
        final Reservation reservation2 = new Reservation(room.getId(),
                LocalDateTime.of(2023, 11, 10, 13, 0), LocalDateTime.of(2023, 11, 10, 15, 30));
        final Reservation reservation3 = new Reservation(room.getId(),
                LocalDateTime.of(2023, 11, 10, 13, 0), LocalDateTime.of(2023, 11, 10, 15, 30));
        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));

        //when
        final List<RoomResponse> response = roomService.findAllAtDate(LocalDate.of(2023, 11, 10), authentication);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response).hasSize(1);
            softAssertions.assertThat(response.get(0).roomId()).isEqualTo(room.getId());
            softAssertions.assertThat(response.get(0).reservations())
                    .hasSize(3)
                    .map(ReservationResponse::isMine).allMatch(isMine -> true);
        });
    }

    private Room createRoom() {
        final CreateRoomRequest request = new CreateRoomRequest("대회의실", "image.com", 15);
        return roomRepository.save(request.toEntity());
    }
}
