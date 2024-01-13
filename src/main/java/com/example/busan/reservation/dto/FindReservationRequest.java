package com.example.busan.reservation.dto;

import java.time.LocalDateTime;

public record FindReservationRequest(LocalDateTime start, LocalDateTime end) {
}
