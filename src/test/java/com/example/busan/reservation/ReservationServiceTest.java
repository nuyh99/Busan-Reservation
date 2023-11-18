package com.example.busan.reservation;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.member.domain.Region;
import com.example.busan.reservation.domain.Reservation;
import com.example.busan.reservation.domain.ReservationRepository;
import com.example.busan.reservation.domain.Status;
import com.example.busan.reservation.dto.CancelReservationRequest;
import com.example.busan.reservation.dto.CreateReservationRequest;
import com.example.busan.reservation.dto.ReservationResponse;
import com.example.busan.reservation.dto.UpdateReservationRequest;
import com.example.busan.reservation.service.ReservationNamedLockFacade;
import com.example.busan.reservation.service.ReservationService;
import com.example.busan.room.domain.Room;
import com.example.busan.room.domain.RoomRepository;
import com.example.busan.room.dto.CreateRoomRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationNamedLockFacade reservationFacade;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @MockBean
    private LoginUserAuditorAware auditorAware;

    @BeforeEach
    void setUp() {
        given(auditorAware.getCurrentAuditor())
                .willReturn(Optional.of("test@gmail.com"));
        databaseCleaner.truncate();
    }

    @Test
    @DisplayName("예약을 생성할 수 있다")
    void create() {
        //given
        final CreateReservationRequest request = new CreateReservationRequest(
                1L, of(3000, 11, 10, 14, 0), of(3000, 11, 10, 15, 0));

        //when
        reservationService.create(request);

        //then
        assertThat(reservationRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("동시적으로 중복 예약을 하면 1개만 생성된다")
    void create_concurrency() throws InterruptedException {
        //given
        final int userCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(userCount);

        //when
        for (int i = 0; i < userCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationFacade.create(
                            new CreateReservationRequest(
                                    1L,
                                    LocalDateTime.of(2050, 11, 10, 13, 0),
                                    LocalDateTime.of(2050, 11, 10, 15, 30)));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        assertThat(reservationRepository.count()).isOne();
    }

    @Test
    @DisplayName("동시적으로 100개의 요청 성공")
    void create_concurrency2() throws InterruptedException {
        //given
        final int userCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(userCount);

        //when
        for (int i = 0; i < userCount; i++) {
            final long roomId = i;
            executorService.submit(() -> {
                try {
                    reservationFacade.create(
                            new CreateReservationRequest(
                                    roomId,
                                    LocalDateTime.of(2050, 11, 10, 13, 0),
                                    LocalDateTime.of(2050, 11, 10, 15, 30)));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        assertThat(reservationRepository.count()).isEqualTo(userCount);
    }

    @Test
    @DisplayName("다른 예약과 시간이 겹치면 생성할 수 없다")
    void create_fail() {
        //given
        reservationService.create(new CreateReservationRequest(
                1L, of(3000, 11, 10, 14, 0), of(3000, 11, 10, 15, 0)));

        final CreateReservationRequest request = new CreateReservationRequest(
                1L, of(3000, 11, 10, 14, 30), of(3000, 11, 10, 17, 0));

        //when, then
        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("본인의 예약을 취소할 수 있다")
    void cancel() {
        //given
        final Reservation reservation = createReservation(1L);
        final CancelReservationRequest request = new CancelReservationRequest("쓰기 싫어요");

        //when
        reservationService.deleteById(reservation.getId(), "test@gmail.com", request);

        //then
        final Reservation canceled = reservationRepository.findById(reservation.getId()).get();
        assertThat(canceled.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    @DisplayName("본인의 예약이 아니면 취소할 때 예외가 발생한다")
    void cancel_fail() {
        //given
        final Reservation reservation = createReservation(1L);
        final CancelReservationRequest request = new CancelReservationRequest("쓰기 싫어요");

        //when
        assertThatThrownBy(() -> reservationService.deleteById(reservation.getId(), "notMine@gmail.com", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("본인의 예약이 아니면 수정할 때 예외가 발생한다")
    void update_fail() {
        //given
        final Reservation reservation = createReservation(1L);
        final UpdateReservationRequest request = new UpdateReservationRequest(
                1L, of(2024, 11, 10, 14, 0), of(2024, 11, 10, 15, 0));

        //when
        assertThatThrownBy(() -> reservationService.update(reservation.getId(), "notMine@gmail.com", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다른 예약과 시간이 겹치면 수정할 수 없다")
    void update_fail2() {
        //given
        final Reservation reservation = createReservation(1L);
        final UpdateReservationRequest request = new UpdateReservationRequest(
                1L, of(2023, 11, 10, 14, 0), of(2023, 11, 10, 15, 0));

        //when
        assertThatThrownBy(() -> reservationService.update(reservation.getId(), "test@gmail.com", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자신이 예약한 목록들을 조회할 수 있다")
    void findAll() {
        //given
        final Room room = createRoom();
        final Member member = createMember();
        createReservation(room.getId());
        createReservation(room.getId());

        //when
        final List<ReservationResponse> response = reservationService.findAll("test@gmail.com", Pageable.ofSize(10));

        //then
        assertSoftly(soft -> {
            soft.assertThat(response).hasSize(2);
            soft.assertThat(response).map(ReservationResponse::cancelReason).allMatch(Objects::isNull);
            soft.assertThat(response).map(ReservationResponse::reservedAt).allMatch(reservedAt -> reservedAt.isBefore(now()));
            soft.assertThat(response).map(ReservationResponse::name).allMatch(name -> name.equals(member.getName()));
            soft.assertThat(response).map(ReservationResponse::roomId).allMatch(roomId -> roomId.equals(room.getId()));
            soft.assertThat(response).map(ReservationResponse::phone).allMatch(phone -> phone.equals(member.getPhone()));
            soft.assertThat(response).map(ReservationResponse::status).allMatch(status -> status == Status.RESERVED);
        });
    }

    @Test
    @DisplayName("자신이 예약한 목록들을 조회할 수 있다 - 취소 목록")
    void findAll2() {
        //given
        final Room room = createRoom();
        final Member member = createMember();
        createCanceledReservation(room.getId());
        createCanceledReservation(room.getId());

        //when
        final List<ReservationResponse> response = reservationService.findAll("test@gmail.com", Pageable.ofSize(10));

        //then
        assertSoftly(soft -> {
            soft.assertThat(response).hasSize(2);
            soft.assertThat(response).map(ReservationResponse::cancelReason).allMatch(reason -> reason.equals("쓰기 싫네요.."));
            soft.assertThat(response).map(ReservationResponse::reservedAt).allMatch(reservedAt -> reservedAt.isBefore(now()));
            soft.assertThat(response).map(ReservationResponse::name).allMatch(name -> name.equals(member.getName()));
            soft.assertThat(response).map(ReservationResponse::roomId).allMatch(roomId -> roomId.equals(room.getId()));
            soft.assertThat(response).map(ReservationResponse::phone).allMatch(phone -> phone.equals(member.getPhone()));
            soft.assertThat(response).map(ReservationResponse::status).allMatch(status -> status == Status.CANCELED);
        });
    }

    private Reservation createReservation(final Long roomId) {
        return reservationRepository.save(
                new Reservation(roomId, of(3000, 11, 10, 14, 0), of(3000, 11, 10, 15, 0)));
    }

    private Reservation createCanceledReservation(final Long roomId) {
        final Reservation reservation = new Reservation(roomId, of(2050, 11, 10, 14, 0), of(2050, 11, 10, 15, 0));
        reservation.cancel("쓰기 싫네요..");
        return reservationRepository.save(reservation);
    }

    private Member createMember() {
        final Member member = new Member("test@gmail.com", "재현", "@password1234", Region.GANGNEUNG, "01012345678", "부경대", new PasswordEncoder());
        return memberRepository.save(member);
    }

    private Room createRoom() {
        final CreateRoomRequest request = new CreateRoomRequest("대회의실", "image.com", 15);
        return roomRepository.save(request.toEntity());
    }
}
