package com.example.busan.reservation.service;

import com.example.busan.reservation.domain.ReservationNamedLockRepository;
import com.example.busan.reservation.dto.CreateReservationRequest;
import com.example.busan.reservation.dto.UpdateReservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReservationNamedLockFacade {

    private static final Logger log = LoggerFactory.getLogger(ReservationNamedLockFacade.class);
    private static final int SUCCESS = 1;

    private final ReservationNamedLockRepository lockRepository;
    private final ReservationService reservationService;

    public ReservationNamedLockFacade(final ReservationNamedLockRepository lockRepository,
                                      final ReservationService reservationService) {
        this.lockRepository = lockRepository;
        this.reservationService = reservationService;
    }

    public void create(final CreateReservationRequest request) {
        processWithRoomIdLocking(
                () -> reservationService.create(request),
                request.roomId());
    }

    private void processWithRoomIdLocking(final Runnable runnable, final Long roomId) {
        try {
            final Integer lock = lockRepository.getLock(roomId);

            if (lock == SUCCESS) {
                runnable.run();
                return;
            }

            log.error("방 번호에 대한 네임드락 획득 실패 | 획득 여부 {} | 방 번호 {}", lock, roomId);
        } finally {
            lockRepository.releaseLock(roomId);
        }
    }

    public void update(final Long id,
                       final String currentMemberEmail,
                       final UpdateReservationRequest request) {
        processWithRoomIdLocking(
                () -> reservationService.update(id, currentMemberEmail, request),
                request.roomId());
    }
}
