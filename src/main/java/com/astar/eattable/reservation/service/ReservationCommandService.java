package com.astar.eattable.reservation.service;

import com.astar.eattable.common.lock.DistributedLock;
import com.astar.eattable.reservation.command.ReservationCreateCommand;
import com.astar.eattable.reservation.event.ReservationCreateEvent;
import com.astar.eattable.reservation.exception.ReservationNotFoundException;
import com.astar.eattable.reservation.exception.TableAvailabilityNotFoundException;
import com.astar.eattable.reservation.model.Reservation;
import com.astar.eattable.reservation.model.TableAvailability;
import com.astar.eattable.reservation.repository.ReservationRepository;
import com.astar.eattable.reservation.repository.TableAvailabilityRepository;
import com.astar.eattable.reservation.validator.ReservationValidator;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
import com.astar.eattable.restaurant.repository.RestaurantTableRepository;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationCommandService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableAvailabilityRepository tableAvailabilityRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;
    private final ReservationLockService reservationLockService;
    private final ApplicationEventPublisher publisher;

    @DistributedLock(key = "#command.restaurantId + ':' + #command.date + ':' + #command.time + ':' + #command.capacity")
    public void createReservation(ReservationCreateCommand command, User currentUser) {
        TableAvailability tableAvailability = tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(command.getRestaurantId(), LocalDate.parse(command.getDate()), LocalTime.parse(command.getTime()), command.getCapacity()).orElseThrow(() -> new TableAvailabilityNotFoundException(command.getRestaurantId(), command.getDate(), command.getTime(), command.getCapacity()));
        reservationValidator.validateRemainTableCount(tableAvailability);

        Reservation reservation = reservationRepository.save(command.toEntity(tableAvailability.getRestaurant(), tableAvailability, currentUser));
        tableAvailability.decreaseRemainingTableCount();

        publisher.publishEvent(new ReservationCreateEvent(reservation.getId(), command, reservation.getRestaurant().getName(), currentUser));
    }

    public void cancelReservation(Long reservationId, User currentUser) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException(reservationId));
        reservationLockService.cancelReservationInternal(reservationId, reservation.getRestaurant().getId(), reservation.getTableAvailability().getDate().toString(), reservation.getTableAvailability().getStartTime().toString(), reservation.getCapacity(), currentUser);
    }
}
