package com.astar.eattable.reservation.validator;

import com.astar.eattable.reservation.exception.NotEnoughTableException;
import com.astar.eattable.reservation.model.TableAvailability;
import org.springframework.stereotype.Component;

@Component
public class ReservationValidator {

    public void validateRemainTableCount(TableAvailability tableAvailability) {
        if (tableAvailability.getRemainingTableCount() <= 0) {
            throw new NotEnoughTableException(tableAvailability.getId());
        }
    }
}
