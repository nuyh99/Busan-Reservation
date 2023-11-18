package com.example.busan.reservation.service;

import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.reservation.domain.Reservation;
import com.example.busan.reservation.domain.ReservationRepository;
import com.example.busan.reservation.dto.CancelReservationRequest;
import com.example.busan.reservation.dto.CreateReservationRequest;
import com.example.busan.reservation.dto.ReservationResponse;
import com.example.busan.reservation.dto.UpdateReservationRequest;
import com.example.busan.room.domain.Room;
import com.example.busan.room.domain.RoomRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    public ReservationService(final ReservationRepository reservationRepository,
                              final MemberRepository memberRepository,
                              final RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void create(final CreateReservationRequest request) {
        validateDuplicated(request.startTime(), request.endTime(), request.roomId());
        reservationRepository.saveAndFlush(request.toEntity());
    }

    private void validateDuplicated(final LocalDateTime startTime, final LocalDateTime endTime, final Long roomId) {
        if (reservationRepository.existDuplicatedTime(startTime, endTime, roomId)) {
            throw new IllegalArgumentException("다른 예약과 겹칠 수 없습니다.");
        }
    }

    @Transactional
    public void deleteById(final Long id, final String currentMemberEmail, final CancelReservationRequest request) {
        final Reservation reservation = reservationRepository.findByIdAndReservationEmail(id, currentMemberEmail)
                .orElseThrow(() -> new IllegalArgumentException("자신이 예약한 회의실만 취소할 수 있습니다."));

        reservation.cancel(request.reason());
    }

    @Transactional
    public void update(final Long id, final String currentMemberEmail, final UpdateReservationRequest request) {
        validateDuplicated(request.startTime(), request.endTime(), request.roomId());
        final Reservation reservation = reservationRepository.findByIdAndReservationEmail(id, currentMemberEmail)
                .orElseThrow(() -> new IllegalArgumentException("자신이 예약한 회의실만 수정할 수 있습니다."));

        reservation.update(request.roomId(), request.startTime(), request.endTime());
    }

    public List<ReservationResponse> findAll(final String currentMemberEmail, final Pageable pageable) {
        final Member member = getMember(currentMemberEmail);
        final List<Reservation> reservations = getReservations(currentMemberEmail, pageable);
        final Map<Long, Room> rooms = getRooms(reservations);

        return reservations.stream()
                .map(reservation -> ReservationResponse.of(reservation, member, rooms.get(reservation.getRoomId())))
                .toList();
    }

    private Member getMember(final String currentMemberEmail) {
        return memberRepository.findById(currentMemberEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로그인 유저입니다."));
    }

    private List<Reservation> getReservations(final String currentMemberEmail, final Pageable pageable) {
        final PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), Sort.by("startTime").descending());

        return reservationRepository.findAllByReservationEmail(currentMemberEmail, pageRequest);
    }

    private Map<Long, Room> getRooms(final List<Reservation> reservations) {
        final List<Long> roomIds = reservations.stream()
                .map(Reservation::getRoomId)
                .distinct()
                .toList();

        return roomRepository.findAllById(roomIds).stream()
                .collect(toMap(Room::getId, Function.identity()));
    }
}
