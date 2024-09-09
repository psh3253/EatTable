package com.astar.eattable.reservation.service;

import com.astar.eattable.reservation.event.ReservationCancelEvent;
import com.astar.eattable.reservation.exception.ReservationNotFoundException;
import com.astar.eattable.reservation.exception.TableAvailabilityNotFoundException;
import com.astar.eattable.reservation.exception.UnauthorizedReservationAccessException;
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
class ReservationLockServiceTest {

    @InjectMocks
    private ReservationLockService reservationLockService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TableAvailabilityRepository tableAvailabilityRepository;

    @Mock
    private ReservationValidator reservationValidator;

    @Mock
    private ApplicationEventPublisher publisher;

    private User user;
    private User notOwnerUser;
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
        notOwnerUser = User.builder()
                .email("test2@test.com")
                .password("test1234")
                .nickname("테스트2")
                .phoneNumber("010-5678-1234")
                .role(Role.ROLE_USER)
                .build();
        notOwnerUser.setIdForTest(2L);
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
                .remainingTableCount(3)
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
    @DisplayName("유효한 예약 정보로 예약을 취소하면 예약이 취소된다.")
    void cancelReservationInternal_withValidInput_cancelReservation() {
        // given
        Long reservationId = 1L;
        Long restaurantId = 1L;
        String date = "2024-09-01";
        String startTime = "10:00";
        int capacity = 2;
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(restaurantId, LocalDate.parse(date), LocalTime.parse(startTime), capacity)).willReturn(Optional.of(tableAvailability));
        willDoNothing().given(reservationValidator).validateReservationOwnerOrRestaurantOwner(reservation, user.getId());

        // when
        reservationLockService.cancelReservationInternal(reservationId, restaurantId, date, startTime, capacity, user);

        // then
        assertThat(reservation.isCanceled()).isTrue();
        assertThat(tableAvailability.getRemainingTableCount()).isEqualTo(4);

        verify(publisher, times(1)).publishEvent(any(ReservationCancelEvent.class));
    }

    @Test
    @DisplayName("예약 정보가 없는 경우 예약 취소를 시도하면 ReservationNotFoundException이 발생한다.")
    void cancelReservationInternal_withInvalidReservationId_throwReservationNotFoundException() {
        // given
        Long reservationId = 1L;
        Long restaurantId = 1L;
        String date = "2024-09-01";
        String startTime = "10:00";
        int capacity = 2;
        given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ReservationNotFoundException.class, () -> reservationLockService.cancelReservationInternal(reservationId, restaurantId, date, startTime, capacity, user));

        verify(publisher, times(0)).publishEvent(any(ReservationCancelEvent.class));
    }

    @Test
    @DisplayName("예약자가 아닌 경우 예약 취소를 시도하면 UnauthorizedReservationAccessException 예외가 발생한다.")
    void cancelReservationInternal_withInvalidUser_throwUnauthorizedReservationAccessException() {
        // given
        Long reservationId = 1L;
        Long restaurantId = 1L;
        String date = "2024-09-01";
        String startTime = "10:00";
        int capacity = 2;
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        willThrow(new UnauthorizedReservationAccessException(reservationId, user.getId())).given(reservationValidator).validateReservationOwnerOrRestaurantOwner(reservation, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedReservationAccessException.class, () -> reservationLockService.cancelReservationInternal(reservationId, restaurantId, date, startTime, capacity, notOwnerUser));

        verify(publisher, times(0)).publishEvent(any(ReservationCancelEvent.class));
    }

    @Test
    @DisplayName("유효하지 않은 예약 정보로 예약을 취소하면 TableAvailabilityNotFoundException이 발생한다.")
    void cancelReservationInternal_withInvalidTableAvailability_throwTableAvailabilityNotFoundException() {
        // given
        Long reservationId = 1L;
        Long restaurantId = 1L;
        String date = "2024-09-01";
        String startTime = "10:00";
        int capacity = 2;
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(restaurantId, LocalDate.parse(date), LocalTime.parse(startTime), capacity)).willReturn(Optional.empty());

        // when & then
        assertThrows(TableAvailabilityNotFoundException.class, () -> reservationLockService.cancelReservationInternal(reservationId, restaurantId, date, startTime, capacity, user));

        verify(publisher, times(0)).publishEvent(any(ReservationCancelEvent.class));
    }
}