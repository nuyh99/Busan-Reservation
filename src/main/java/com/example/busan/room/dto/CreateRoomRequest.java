package com.example.busan.room.dto;

import com.example.busan.room.domain.Room;

public record CreateRoomRequest(String name, String image, Integer maxPeopleCount) {

    public Room toEntity() {
        return new Room(name, image, maxPeopleCount);
    }
}
