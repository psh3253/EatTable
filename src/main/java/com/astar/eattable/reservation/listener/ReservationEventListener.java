package com.astar.eattable.reservation.listener;

import com.astar.eattable.reservation.service.ReservationCommandService;
import com.astar.eattable.restaurant.event.RestaurantCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationEventListener {
    private final ReservationCommandService reservationCommandService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRestaurantCreateEvent(RestaurantCreateEvent event) {
        reservationCommandService.initRestaurantTable(event.getRestaurantId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRestaurantCreateEventAsync(RestaurantCreateEvent event) {
        reservationCommandService.createTableAvailabilities(event.getRestaurantId());
    }
}
