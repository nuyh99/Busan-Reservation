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
        assertThatThrownBy(() -> new Reservation(1L, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("시작 시각보다 끝 시각이 앞이면 예외가 발생한다")
    void construct_fail2() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2023, 11, 10, 15, 0);
        final LocalDateTime endTime = LocalDateTime.of(2023, 11, 10, 14, 1);

        //when, then
        assertThatThrownBy(() -> new Reservation(1L, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("정상 예약")
    void construct() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2023, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2023, 11, 10, 15, 30);

        //when, then
        assertDoesNotThrow(() -> new Reservation(1L, startTime, endTime));
    }

    @Test
    @DisplayName("예약을 취소할 때 현재 사용 중이면 예외가 발생한다")
    void cancel_fail() {
        //given
        final LocalDateTime startTime = LocalDateTime.now();
        final LocalDateTime endTime = startTime.plusHours(3);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() -> reservation.cancel("사용 안할래요"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약을 취소할 때 이미 사용을 했으면 예외가 발생한다")
    void cancel_fail2() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2022, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2022, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() -> reservation.cancel("사용 안할래요"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약을 취소할 때 취소 이유가 없으면 예외가 발생한다")
    void cancel_fail3() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() -> reservation.cancel(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
