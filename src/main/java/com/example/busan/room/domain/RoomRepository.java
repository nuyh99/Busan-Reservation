package com.example.busan.room.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
            FROM Room r
            ORDER BY r.sequence
            """)
    List<Room> findAllOrderBySequence();
}
