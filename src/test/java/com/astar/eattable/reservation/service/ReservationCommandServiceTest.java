package com.astar.eattable.reservation.service;

import com.astar.eattable.reservation.command.ReservationCreateCommand;
import com.astar.eattable.reservation.event.ReservationCreateEvent;
import com.astar.eattable.reservation.exception.NotEnoughTableException;
import com.astar.eattable.reservation.exception.TableAvailabilityNotFoundException;
import com.astar.eattable.reservation.model.Reservation;
import com.astar.eattable.reservation.model.TableAvailability;
import com.astar.eattable.reservation.repository.ReservationRepository;
import com.astar.eattable.reservation.repository.TableAvailabilityRepository;
import com.astar.eattable.reservation.validator.ReservationValidator;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.model.RestaurantTable;
import com.astar.eattable.user.model.Role;
import com.astar.eattable.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class ReservationCommandServiceTest {

    @InjectMocks
    private ReservationCommandService reservationCommandService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TableAvailabilityRepository tableAvailabilityRepository;

    @Mock
    private ReservationValidator reservationValidator;

    @Mock
    private ReservationLockService reservationLockService;

    @Mock
    private ApplicationEventPublisher publisher;

    private User user;
    private Restaurant restaurant;
    private RestaurantTable restaurantTable;
    private TableAvailability tableAvailability;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .password("test1234")
                .nickname("테스트")
                .phoneNumber("010-1234-5678")
                .role(Role.ROLE_USER)
                .build();
        user.setIdForTest(1L);
        restaurant = Restaurant.builder()
                .name("테스트 식당")
                .description("맛있는 식당")
                .address("서울시 강남구")
                .phone("02-1234-5678")
                .imageUrl("이미지 URL")
                .categoryName("한식")
                .latitude(37.123456)
                .longitude(127.123456)
                .user(user)
                .build();
        restaurant.setIdForTest(1L);
        restaurantTable = RestaurantTable.builder()
                .capacity(2)
                .count(5)
                .restaurant(restaurant)
                .build();
        restaurantTable.setIdForTest(1L);
        tableAvailability = TableAvailability.builder()
                .restaurant(restaurant)
                .date(LocalDate.parse("2024-09-01"))
                .startTime(LocalTime.parse("10:00"))
                .endTime(LocalTime.parse("12:00"))
                .restaurantTable(restaurantTable)
                .remainingTableCount(5)
                .build();
        reservation = Reservation.builder()
                .restaurant(restaurant)
                .tableAvailability(tableAvailability)
                .user(user)
                .tableAvailability(tableAvailability)
                .capacity(2)
                .request("요청 사항")
                .build();
    }

    @Test
    @DisplayName("유효한 예약 정보로 예약을 생성하면 예약이 생성된다.")
    void createReservation_withValidInput_createReservation() {
        // given
        ReservationCreateCommand command = new ReservationCreateCommand(1L, "2024-09-01", "10:00", 2, "요청 사항");
        given(tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(1L, LocalDate.parse("2024-09-01"), LocalTime.parse("10:00"), 2)).willReturn(Optional.of(tableAvailability));
        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);
        willDoNothing().given(reservationValidator).validateRemainTableCount(tableAvailability);

        // when
        reservationCommandService.createReservation(command, user);

        // then
        assertThat(tableAvailability.getRemainingTableCount()).isEqualTo(4);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(publisher, times(1)).publishEvent(any(ReservationCreateEvent.class));
    }

    @Test
    @DisplayName("유효하지 않은 예약 정보로 예약을 생성하면 TableAvailabilityNotFoundException이 발생한다.")
    void createReservation_withInvalidInput_throwTableAvailabilityNotFoundException() {
        // given
        ReservationCreateCommand command = new ReservationCreateCommand(1L, "2024-09-01", "10:00", 2, "요청 사항");
        given(tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(1L, LocalDate.parse("2024-09-01"), LocalTime.parse("10:00"), 2)).willReturn(Optional.empty());

        // when & then
        assertThrows(TableAvailabilityNotFoundException.class, () -> reservationCommandService.createReservation(command, user));

        verify(reservationRepository, times(0)).save(any(Reservation.class));
        verify(publisher, times(0)).publishEvent(any(ReservationCreateEvent.class));
    }

    @Test
    @DisplayName("남은 테이블이 없는 경우 예약을 생성하면 NotEnoughTableException이 발생한다.")
    void createReservation_withNotEnoughTable_throwNotEnoughTableException() {
        // given
        ReservationCreateCommand command = new ReservationCreateCommand(1L, "2024-09-01", "10:00", 2, "요청 사항");
        given(tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(1L, LocalDate.parse("2024-09-01"), LocalTime.parse("10:00"), 2)).willReturn(Optional.of(tableAvailability));
        willThrow(new NotEnoughTableException(1L)).given(reservationValidator).validateRemainTableCount(tableAvailability);

        // when & then
        assertThrows(NotEnoughTableException.class, () -> reservationCommandService.createReservation(command, user));

        verify(reservationRepository, times(0)).save(any(Reservation.class));
        verify(publisher, times(0)).publishEvent(any(ReservationCreateEvent.class));
    }

    @Test
    @DisplayName("예약을 취소하면 예약이 취소된다.")
    void cancelReservation_withValidInput_cancelReservation() {
        // given
        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));

        // when
        reservationCommandService.cancelReservation(1L, user);

        // then
        verify(reservationRepository, times(1)).findById(1L);
    }
}