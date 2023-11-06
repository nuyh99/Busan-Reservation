package com.example.busan.reservation.dto;

import com.example.busan.reservation.domain.Reservation;

import java.time.LocalDateTime;

public record CreateReservationRequest(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {

    public Reservation toEntity() {
        return new Reservation(roomId, startTime, endTime);
    }
}
