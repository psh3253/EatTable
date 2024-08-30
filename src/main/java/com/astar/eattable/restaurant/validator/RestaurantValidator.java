package com.astar.eattable.restaurant.validator;

import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.astar.eattable.restaurant.exception.ClosedPeriodOverlapException;
import com.astar.eattable.restaurant.exception.ClosedPeriodPastException;
import com.astar.eattable.restaurant.exception.UnauthorizedRestaurantAccessException;
import com.astar.eattable.restaurant.model.ClosedPeriod;
import com.astar.eattable.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RestaurantValidator {
    public void validateRestaurantOwner(Restaurant restaurant, Long userId) {
        if (!restaurant.getUser().getId().equals(userId)) {
            throw new UnauthorizedRestaurantAccessException(restaurant.getId(), userId);
        }
    }

    public void validateRestaurantOwner(RestaurantDocument document, Long userId) {
        if (!document.getUserId().equals(userId)) {
            throw new UnauthorizedRestaurantAccessException(document.getId(), userId);
        }
    }

    public void validateNoOverlapClosedPeriod(List<ClosedPeriod> closedPeriods, String startDate, String endDate, Long restaurantId) {
        closedPeriods.forEach(closedPeriod -> {
            if ((closedPeriod.isOverLap(startDate, endDate))) {
                throw new ClosedPeriodOverlapException(restaurantId, startDate, endDate);
            }
        });
    }

    public void validateClosePeriodNotBeforeToday(String startDate) {
        LocalDate today = LocalDate.now();
        LocalDate start = LocalDate.parse(startDate);
        if (start.isBefore(today)) {
            throw new ClosedPeriodPastException(startDate);
        }
    }
}
