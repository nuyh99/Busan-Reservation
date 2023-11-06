package com.example.busan.reservation;

import com.example.busan.reservation.domain.Reservation;
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
                SELECT 1
                FROM Reservation r
                WHERE NOT (r.endTime <= :startTime OR r.startTime >= :endTime)
            )
            """)
    boolean existDuplicatedTime(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    Optional<Reservation> findByIdAndReservationEmail(Long id, String email);
}
