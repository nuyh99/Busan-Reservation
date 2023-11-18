package com.example.busan.reservation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationNamedLockRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT get_lock(:roomId, 10)", nativeQuery = true)
    Integer getLock(@Param("roomId") Long roomId);

    @Query(value = "SELECT release_lock(:roomId)", nativeQuery = true)
    void releaseLock(@Param("roomId") Long roomId);
}
