package com.astar.eattable.reservation.listener;

import com.astar.eattable.reservation.service.ReservationService;
import com.astar.eattable.restaurant.event.RestaurantCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationEventListener {
    private final ReservationService reservationService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRestaurantCreateEvent(RestaurantCreateEvent event) {
        reservationService.initRestaurantTable(event.getRestaurantId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRestaurantCreateEventAsync(RestaurantCreateEvent event) {
        reservationService.createTableAvailability(event.getRestaurantId());
    }
}
