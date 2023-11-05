package com.example.busan.reservation;

import com.example.busan.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    void deleteAllByRoomId(Long roomId);
}
