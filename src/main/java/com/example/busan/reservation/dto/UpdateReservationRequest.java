package com.example.busan.reservation.dto;

import java.time.LocalDateTime;

public record UpdateReservationRequest(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
}
