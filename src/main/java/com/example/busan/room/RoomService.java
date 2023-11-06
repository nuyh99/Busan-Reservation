package com.example.busan.room;

import com.example.busan.auth.dto.Authentication;
import com.example.busan.reservation.ReservationRepository;
import com.example.busan.reservation.domain.Reservation;
import com.example.busan.room.domain.Room;
import com.example.busan.room.dto.CreateRoomRequest;
import com.example.busan.room.dto.ReservationResponse;
import com.example.busan.room.dto.RoomResponse;
import com.example.busan.room.dto.UpdateRoomRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

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

    public List<RoomResponse> findAllAtDate(final LocalDate date, final Authentication authentication) {
        String currentMemberEmail = getCurrentMemberEmail(authentication);

        final Map<Long, List<ReservationResponse>> reservations = getReservations(date)
                .stream()
                .collect(groupingBy(
                        Reservation::getRoomId,
                        createDtoWith(currentMemberEmail)));

        return roomRepository.findAll()
                .stream()
                .map(room -> RoomResponse.of(room, reservations.getOrDefault(room.getId(), List.of())))
                .toList();
    }

    private String getCurrentMemberEmail(final Authentication authentication) {
        if (nonNull(authentication)) {
            return authentication.email();
        }
        return null;
    }

    private List<Reservation> getReservations(final LocalDate date) {
        if (isNull(date)) {
            return reservationRepository.findAllByStartTimeDate(LocalDate.now());
        }

        return reservationRepository.findAllByStartTimeDate(date);
    }

    private Collector<Reservation, ?, List<ReservationResponse>> createDtoWith(final String finalCurrentMemberEmail) {
        return mapping(reservation -> ReservationResponse.of(reservation, finalCurrentMemberEmail), toList());
    }
}