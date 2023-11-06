package com.example.busan.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReservationTest {

    @Test
    @DisplayName("최대 3시간을 넘기면 예약 시 예외가 발생한다")
    void construct_fail() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 16, 1);

        //when, then
        assertThatThrownBy(() -> new Reservation(1L, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("시작 시각보다 끝 시각이 앞이면 예외가 발생한다")
    void construct_fail2() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 15, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 14, 1);

        //when, then
        assertThatThrownBy(() -> new Reservation(1L, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("현재보다 미래의 예약이 아니면 예외가 발생한다")
    void construct_fail3() {
        //given
        final LocalDateTime startTime = LocalDateTime.now();
        final LocalDateTime endTime = startTime.plusHours(2);

        //when, then
        assertThatThrownBy(() -> new Reservation(1L, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("정상 예약")
    void construct() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);

        //when, then
        assertDoesNotThrow(() -> new Reservation(1L, startTime, endTime));
    }

    @Test
    @DisplayName("예약을 취소할 때 취소 이유가 없으면 예외가 발생한다")
    void cancel_fail2() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() -> reservation.cancel(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약 수정하기")
    void update() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when
        reservation.update(2L, LocalDateTime.of(2050, 11, 11, 13, 0), LocalDateTime.of(2050, 11, 11, 15, 0));

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(reservation.getRoomId()).isEqualTo(2L);
            softAssertions.assertThat(reservation.getStartTime()).isEqualTo(LocalDateTime.of(2050, 11, 11, 13, 0));
            softAssertions.assertThat(reservation.getEndTime()).isEqualTo(LocalDateTime.of(2050, 11, 11, 15, 0));
        });
    }

    @Test
    @DisplayName("예약을 수정할 때 취소 상태이면 예외가 발생한다")
    void update_fail() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);
        reservation.cancel("사용 안할래요");

        //when, then
        assertThatThrownBy(() -> reservation.update(2L, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약을 수정할 때 미래의 시각으로만 가능하다")
    void update_fail3() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() ->
                reservation.update(2L, LocalDateTime.of(2022, 11, 10, 13, 0), LocalDateTime.of(2022, 11, 10, 14, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약을 수정할 때 최대 3시간까지만 가능하다")
    void update_fail4() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() ->
                reservation.update(2L, LocalDateTime.of(2050, 11, 10, 13, 0), LocalDateTime.of(2050, 11, 10, 16, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약을 수정할 때 시작 시각이 종료 시각보다 이후면 예외가 발생한다")
    void update_fail5() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() ->
                reservation.update(2L, LocalDateTime.of(2050, 11, 10, 14, 0), LocalDateTime.of(2050, 11, 10, 13, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약을 수정할 때 회의실 ID가 없으면 예외가 발생한다")
    void update_fail6() {
        //given
        final LocalDateTime startTime = LocalDateTime.of(2050, 11, 10, 13, 0);
        final LocalDateTime endTime = LocalDateTime.of(2050, 11, 10, 15, 30);
        final Reservation reservation = new Reservation(1L, startTime, endTime);

        //when, then
        assertThatThrownBy(() ->
                reservation.update(null, LocalDateTime.of(2050, 11, 10, 12, 0), LocalDateTime.of(2050, 11, 10, 13, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
