package com.astar.eattable.reservation.validator;

import com.astar.eattable.reservation.document.ReservationDocument;
import com.astar.eattable.reservation.exception.NotEnoughTableException;
import com.astar.eattable.reservation.exception.UnauthorizedReservationAccessException;
import com.astar.eattable.reservation.model.Reservation;
import com.astar.eattable.reservation.model.TableAvailability;
import com.astar.eattable.restaurant.document.RestaurantDocument;
import org.springframework.stereotype.Component;

@Component
public class ReservationValidator {

    public void validateRemainTableCount(TableAvailability tableAvailability) {
        if (tableAvailability.getRemainingTableCount() <= 0) {
            throw new NotEnoughTableException(tableAvailability.getId());
        }
    }

    public void validateReservationOwnerOrRestaurantOwner(ReservationDocument reservationDocument, RestaurantDocument restaurantDocument, Long userId) {
        if (!reservationDocument.getUserId().equals(userId) && !restaurantDocument.getUserId().equals(userId)) {
            throw new UnauthorizedReservationAccessException(reservationDocument.getId(), userId);
        }
    }

    public void validateReservationOwnerOrRestaurantOwner(Reservation reservation, Long userId) {
        if (!reservation.getUser().getId().equals(userId) && !reservation.getRestaurant().getUser().getId().equals(userId)) {
            throw new UnauthorizedReservationAccessException(reservation.getId(), userId);
        }
    }
}
