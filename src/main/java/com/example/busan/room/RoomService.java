package com.example.busan.room;

import com.example.busan.reservation.ReservationRepository;
import com.example.busan.room.domain.Room;
import com.example.busan.room.dto.CreateRoomRequest;
import com.example.busan.room.dto.RoomResponse;
import com.example.busan.room.dto.UpdateRoomRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(final RoomRepository roomRepository, final ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void save(final CreateRoomRequest request) {
        final Room room = request.toEntity();
        roomRepository.save(room);
    }

    @Transactional
    public void update(final Long roomId, final UpdateRoomRequest request) {
        final Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의실입니다."));
        room.update(request.name(), request.image(), request.maxPeopleCount());
    }

    @Transactional
    public void deleteById(final Long roomId) {
        reservationRepository.deleteAllByRoomId(roomId);
        roomRepository.deleteById(roomId);
    }

    public List<RoomResponse> findAll() {
        throw new UnsupportedOperationException();
    }
}
