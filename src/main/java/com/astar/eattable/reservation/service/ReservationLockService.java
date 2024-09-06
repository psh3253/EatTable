package com.astar.eattable.reservation.service;

import com.astar.eattable.common.lock.DistributedLock;
import com.astar.eattable.reservation.event.ReservationCancelEvent;
import com.astar.eattable.reservation.exception.ReservationNotFoundException;
import com.astar.eattable.reservation.exception.TableAvailabilityNotFoundException;
import com.astar.eattable.reservation.model.Reservation;
import com.astar.eattable.reservation.model.TableAvailability;
import com.astar.eattable.reservation.repository.ReservationRepository;
import com.astar.eattable.reservation.repository.TableAvailabilityRepository;
import com.astar.eattable.reservation.validator.ReservationValidator;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationLockService {
    private final ReservationRepository reservationRepository;
    private final TableAvailabilityRepository tableAvailabilityRepository;
    private final ReservationValidator reservationValidator;
    private final ApplicationEventPublisher publisher;

    @DistributedLock(key = "#restaurantId + ':' + #date + ':' + #startTime + ':' + #capacity")
    public void cancelReservationInternal(Long reservationId, Long restaurantId, String date, String startTime, int capacity, User currentUser) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException(reservationId));
        reservationValidator.validateReservationOwnerOrRestaurantOwner(reservation, currentUser.getId());
        reservation.cancel();

        TableAvailability tableAvailability = tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(reservation.getRestaurant().getId(), reservation.getTableAvailability().getDate(), reservation.getTableAvailability().getStartTime(), reservation.getCapacity()).orElseThrow(() -> new TableAvailabilityNotFoundException(reservation.getRestaurant().getId(), reservation.getTableAvailability().getDate().toString(), reservation.getTableAvailability().getStartTime().toString(), reservation.getCapacity()));
        tableAvailability.increaseRemainingTableCount();

        publisher.publishEvent(new ReservationCancelEvent(reservationId, currentUser));
    }
}
