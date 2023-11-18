package com.example.busan.reservation.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    void deleteAllByRoomId(Long roomId);

    @Query("""
            FROM Reservation r
            WHERE DATE_FORMAT (r.startTime, '%Y-%m-%d') = :date
            """)
    List<Reservation> findAllByStartTimeDate(@Param("date") LocalDate date);

    @Query(value = """
            SELECT EXISTS (
                FROM Reservation r
                WHERE NOT (r.endTime <= :startTime OR r.startTime >= :endTime)
                AND r.roomId = :roomId
            )
            """)
    boolean existDuplicatedTime(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime,
                                @Param("roomId") Long roomId);

    Optional<Reservation> findByIdAndReservationEmail(Long id, String email);

    List<Reservation> findAllByReservationEmail(String reservationEmail, Pageable pageable);
}
