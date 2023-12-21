package com.example.busan.room.domain;

import com.example.busan.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoomRepositoryTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        databaseCleaner.truncate();
    }

    @Test
    @DisplayName("회의실을 시퀀스 오름차순으로 가져온다")
    void ordered() {
        //given
        roomRepository.save(new Room("회의실1", "image", 4, 5));
        roomRepository.save(new Room("회의실2", "image", 4, 1));
        roomRepository.save(new Room("회의실3", "image", 4, 3));

        //when
        final List<Room> rooms = roomRepository.findAllOrderBySequence();

        //then
        assertThat(rooms)
                .map(Room::getSequence)
                .isSortedAccordingTo(Comparator.naturalOrder());
    }
}
