package com.example.busan.reservation;

import com.example.busan.DatabaseCleaner;
import com.example.busan.reservation.domain.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @MockBean
    private LoginUserAuditorAware auditorAware;

    @BeforeEach
    void cleanDatabase() {
        databaseCleaner.truncate();
    }

    @Test
    @Transactional
    @DisplayName("방 번호에 해당하는 모든 예약을 제거할 수 있다")
    void deleteAllByRoomId() {
        //given
        given(auditorAware.getCurrentAuditor())
                .willReturn(Optional.of("test@naver.com"));

        final long roomId = 1L;
        final Reservation reservation1 = new Reservation(roomId,
                LocalDateTime.of(2023, 11, 10, 13, 0), LocalDateTime.of(2023, 11, 10, 15, 30));
        final Reservation reservation2 = new Reservation(roomId,
                LocalDateTime.of(2023, 11, 10, 13, 0), LocalDateTime.of(2023, 11, 10, 15, 30));
        final Reservation reservation3 = new Reservation(roomId,
                LocalDateTime.of(2023, 11, 10, 13, 0), LocalDateTime.of(2023, 11, 10, 15, 30));
        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));

        //when
        reservationRepository.deleteAllByRoomId(roomId);

        //then
        assertThat(reservationRepository.findAll()).isEmpty();
    }
}
