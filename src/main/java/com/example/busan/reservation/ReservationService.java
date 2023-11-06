package com.example.busan.reservation;

import com.example.busan.reservation.dto.CreateReservationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void create(final CreateReservationRequest request) {
        if (reservationRepository.existDuplicatedTime(request.startTime(), request.endTime())) {
            throw new IllegalArgumentException("다른 예약과 겹칠 수 없습니다.");
        }
        reservationRepository.save(request.toEntity());
    }
}
