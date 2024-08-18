package com.astar.eattable.reservation.service;

import com.astar.eattable.reservation.document.MonthlyAvailabilityDocument;
import com.astar.eattable.reservation.dto.MonthlyAvailabilityDTO;
import com.astar.eattable.reservation.exception.MonthlyAvailabilityNotFoundException;
import com.astar.eattable.reservation.repository.MonthlyAvailabilityMongoRepository;
import com.astar.eattable.restaurant.payload.ClosedPeriodCreateEventPayload;
import com.astar.eattable.restaurant.repository.ClosedPeriodMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationQueryService {

    private final MonthlyAvailabilityMongoRepository monthlyAvailabilityMongoRepository;
    private final ClosedPeriodMongoRepository closedPeriodMongoRepository;

    public MonthlyAvailabilityDTO getMonthlyAvailability(Long restaurantId, Integer year, Integer month) {
        MonthlyAvailabilityDocument monthlyAvailabilityDocument = monthlyAvailabilityMongoRepository.findById(restaurantId + "-" + year + "-" + month)
                .orElseThrow(() -> new MonthlyAvailabilityNotFoundException(restaurantId, year, month));
        return new MonthlyAvailabilityDTO(monthlyAvailabilityDocument);
    }

    public void initMonthlyAvailability(Long restaurantId) {
        LocalDate currendDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(31);
        int currentMonth = currendDate.getMonthValue();
        MonthlyAvailabilityDocument monthlyAvailabilityDocument = new MonthlyAvailabilityDocument(restaurantId, currendDate.getYear(), currendDate.getMonthValue());

        while (currendDate.isBefore(endDate)) {
            if (!isDateInClosedPeriod(restaurantId, currendDate.toString())) {
                monthlyAvailabilityDocument.addAvailableDate(currendDate.toString());
            }
            currendDate = currendDate.plusDays(1);

            if (currendDate.getMonthValue() != currentMonth) {
                monthlyAvailabilityMongoRepository.save(monthlyAvailabilityDocument);
                monthlyAvailabilityDocument = new MonthlyAvailabilityDocument(restaurantId, currendDate.getYear(), currendDate.getMonthValue());
                currentMonth = currendDate.getMonthValue();
            }
        }
        monthlyAvailabilityMongoRepository.save(monthlyAvailabilityDocument);
    }

    private boolean isDateInClosedPeriod(Long restaurantId, String date) {
        return closedPeriodMongoRepository.findClosedPeriod(restaurantId, date).isPresent();
    }

    public void initAllMonthlyAvailabilities(List<Long> restaurantIds) {
        restaurantIds.forEach(this::initMonthlyAvailability);
    }

    public void updateMonthlyAvailability(Long restaurantId) {
        List<MonthlyAvailabilityDocument> monthlyAvailabilityDocuments = monthlyAvailabilityMongoRepository.findAllByRestaurantId(restaurantId);
        monthlyAvailabilityMongoRepository.deleteAll(monthlyAvailabilityDocuments);
        initMonthlyAvailability(restaurantId);
    }
}
