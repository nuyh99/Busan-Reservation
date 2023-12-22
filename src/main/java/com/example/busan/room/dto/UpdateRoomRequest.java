package com.example.busan.room.dto;

public record UpdateRoomRequest(String name, String image, Integer maxPeopleCount, Integer sequence) {
}
