package com.example.busan.room.dto;

import com.example.busan.reservation.domain.Reservation;
import com.example.busan.reservation.domain.Status;

import java.time.LocalTime;

public record ReservationResponse(Long reservationId, LocalTime startTime, LocalTime endTime, boolean isMine,
                                  Status status, String company, String reservedName) {

    public static ReservationResponse of(final Reservation reservation,
                                         final String currentMemberEmail,
                                         final String company,
                                         final String reservedName) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStartTime().toLocalTime(),
                reservation.getEndTime().toLocalTime(),
                reservation.getReservationEmail().equals(currentMemberEmail),
                reservation.getStatus(),
                company,
                reservedName);
    }
}
