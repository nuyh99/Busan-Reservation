package com.example.busan.reservation.dto;

import com.example.busan.member.domain.Member;
import com.example.busan.reservation.domain.Reservation;
import com.example.busan.reservation.domain.Status;
import com.example.busan.room.domain.Room;

import java.time.LocalDateTime;

public record ReservationResponse(Long id, Status status, String cancelReason, LocalDateTime startTime,
                                  LocalDateTime endTime, String name, String phone, LocalDateTime reservedAt,
                                  Long roomId, String roomName) {

    public static ReservationResponse of(final Reservation reservation, final Member member, final Room room) {
        return new ReservationResponse(
                reservation.getId(), reservation.getStatus(), reservation.getCancelReason(),
                reservation.getStartTime(), reservation.getEndTime(), member.getName(), member.getPhone(),
                reservation.getCreatedTime(), room.getId(), room.getName());
    }
}
