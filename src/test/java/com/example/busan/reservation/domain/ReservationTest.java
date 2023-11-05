package com.example.busan.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReservationTest {

    @Test
    @DisplayName("최대 3시간을 넘기면 예약 시 예외가 발생한다")
    void construct_fail() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2023, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2023, 11, 10, 16, 1);

        //when, then
        assertThatThrownBy(() -> new Reservation(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("시작 시각보다 끝 시각이 앞이면 예외가 발생한다")
    void construct_fail2() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2023, 11, 10, 15, 0);
        final LocalDateTime endTime = LocalDateTime.of(2023, 11, 10, 14, 1);

        //when, then
        assertThatThrownBy(() -> new Reservation(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("정상 예약")
    void construct() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2023, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2023, 11, 10, 15, 30);

        //when, then
        assertDoesNotThrow(() -> new Reservation(startTime, endTime));
    }
}
