package com.example.busan.room;

import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.reservation.domain.Reservation;
import com.example.busan.reservation.domain.ReservationRepository;
import com.example.busan.room.domain.Room;
import com.example.busan.room.domain.RoomRepository;
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

    private static final String DELETED_MEMBER_COMPANY = "";
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public RoomService(final RoomRepository roomRepository,
                       final ReservationRepository reservationRepository,
                       MemberRepository memberRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
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
        room.update(request.name(), request.image(), request.maxPeopleCount(), request.sequence());
    }

    @Transactional
    public void deleteById(final Long roomId) {
        reservationRepository.deleteAllByRoomId(roomId);
        roomRepository.deleteById(roomId);
    }

    public List<RoomResponse> findAllAtDate(final LocalDate date, final Authentication authentication) {
        final String currentMemberEmail = getCurrentMemberEmail(authentication);

        final Map<Long, List<ReservationResponse>> reservations = getReservations(date)
                .stream()
                .collect(groupingBy(
                        Reservation::getRoomId,
                        createDtoWith(currentMemberEmail)));

        return roomRepository.findAllOrderBySequence()
                .stream()
                .map(room -> RoomResponse.of(room, reservations.getOrDefault(room.getId(), List.of())))
                .toList();
    }

    private String getCurrentMemberEmail(final Authentication authentication) {
        if (nonNull(authentication)) {
            return authentication.email();
        }

        return "unLoggedIn";
    }

    private List<Reservation> getReservations(final LocalDate date) {
        if (isNull(date)) {
            return reservationRepository.findAllByStartTimeDate(LocalDate.now());
        }

        return reservationRepository.findAllByStartTimeDate(date);
    }

    private Collector<Reservation, ?, List<ReservationResponse>> createDtoWith(final String currentMemberEmail) {
        return mapping(reservation -> {
            final String reservedCompany = memberRepository.findById(currentMemberEmail)
                    .map(Member::getCompany)
                    .orElse(DELETED_MEMBER_COMPANY);

            return ReservationResponse.of(reservation, currentMemberEmail, reservedCompany);
        }, toList());
    }
}
