package com.example.busan.room.dto;

import com.example.busan.room.domain.Room;

import java.util.List;

public record RoomResponse(Long roomId, String name, String image, int maxPeopleCount,
                           List<ReservationResponse> reservations, int sequence) {

    public static RoomResponse of(final Room room, final List<ReservationResponse> reservations) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getImage(),
                room.getMaxPeopleCount(),
                reservations,
                room.getSequence());
    }
}
