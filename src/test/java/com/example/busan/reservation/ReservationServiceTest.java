package com.example.busan.reservation;

import com.example.busan.DatabaseCleaner;
import com.example.busan.reservation.dto.CreateReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @MockBean
    private LoginUserAuditorAware auditorAware;

    @BeforeEach
    void setUp() {
        given(auditorAware.getCurrentAuditor())
                .willReturn(Optional.of("test@gmail.com"));
        databaseCleaner.truncate();
    }

    @Test
    @DisplayName("예약을 생성할 수 있다")
    void create() {
        //given
        final CreateReservationRequest request = new CreateReservationRequest(
                1L, LocalDateTime.of(2023, 11, 10, 14, 0), LocalDateTime.of(2023, 11, 10, 15, 0));

        //when
        reservationService.create(request);

        //then
        assertThat(reservationRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("다른 예약과 시간이 겹치면 생성할 수 없다")
    void create_fail() {
        //given
        reservationService.create(new CreateReservationRequest(
                1L, LocalDateTime.of(2023, 11, 10, 14, 0), LocalDateTime.of(2023, 11, 10, 15, 0)));

        final CreateReservationRequest request = new CreateReservationRequest(
                1L, LocalDateTime.of(2023, 11, 10, 14, 30), LocalDateTime.of(2023, 11, 10, 17, 0));

        //when, then
        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
