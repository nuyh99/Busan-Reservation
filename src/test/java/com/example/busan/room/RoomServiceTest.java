package com.example.busan.room;

import com.example.busan.DatabaseCleaner;
import com.example.busan.room.domain.Room;
import com.example.busan.room.dto.CreateRoomRequest;
import com.example.busan.room.dto.UpdateRoomRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest
class RoomServiceTest {

    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private DatabaseCleaner cleaner;

    @BeforeEach
    void cleanUp() {
        cleaner.truncate();
    }

    @Test
    @DisplayName("회의실 저장")
    void save() {
        //given
        final CreateRoomRequest request = new CreateRoomRequest("대회의실", "image.com", 15);

        //when
        roomService.save(request);

        //then
        assertThat(roomRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("회의실 삭제")
    void deleteById() {
        //given
        final Room room = createRoom();

        //when
        roomService.deleteById(room.getId());

        //then
        assertThat(roomRepository.findById(room.getId())).isEmpty();
    }

    @Test
    @DisplayName("회의실 업데이트")
    void update() {
        //given
        final Room room = createRoom();
        final UpdateRoomRequest request = new UpdateRoomRequest("updated", "newImage", 100);

        //when
        roomService.update(room.getId(), request);

        //then
        final Room updated = roomRepository.findById(room.getId()).get();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(updated.getName()).isEqualTo("updated");
            softAssertions.assertThat(updated.getImage()).isEqualTo("newImage");
            softAssertions.assertThat(updated.getMaxPeopleCount()).isEqualTo(100);
        });
    }

    private Room createRoom() {
        final CreateRoomRequest request = new CreateRoomRequest("대회의실", "image.com", 15);
        return roomRepository.save(request.toEntity());
    }
}
