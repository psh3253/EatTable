package com.astar.eattable.reservation.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.common.service.CommonService;
import com.astar.eattable.reservation.document.MonthlyAvailabilityDocument;
import com.astar.eattable.reservation.document.TableAvailabilityDocument;
import com.astar.eattable.reservation.document.TimeAvailabilityDocument;
import com.astar.eattable.reservation.dto.MonthlyAvailabilityDTO;
import com.astar.eattable.reservation.dto.TableAvailabilityDTO;
import com.astar.eattable.reservation.exception.MonthlyAvailabilityNotFoundException;
import com.astar.eattable.reservation.exception.TableAvailabilityNotFoundException;
import com.astar.eattable.reservation.repository.MonthlyAvailabilityMongoRepository;
import com.astar.eattable.reservation.repository.TableAvailabilityMongoRepository;
import com.astar.eattable.restaurant.document.BusinessHoursDocument;
import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.repository.ClosedPeriodMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantDetailsMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReservationQueryService {

    private final MonthlyAvailabilityMongoRepository monthlyAvailabilityMongoRepository;
    private final ClosedPeriodMongoRepository closedPeriodMongoRepository;
    private final RestaurantDetailsMongoRepository restaurantDetailsMongoRepository;
    private final TableAvailabilityMongoRepository tableAvailabilityMongoRepository;
    private final CommonService commonService;

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

    public void initTableAvailability(Long restaurantId) {
        List<MonthlyAvailabilityDocument> monthlyAvailabilityDocuments = monthlyAvailabilityMongoRepository.findAllByRestaurantId(restaurantId);
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        List<BusinessHoursDocument> businessHoursDocuments = restaurantDetailsDocument.getBusinessHours();
        Map<Day, BusinessHoursDocument> businessHoursMap = businessHoursDocuments.stream().collect(Collectors.toMap(BusinessHoursDocument::getDay, businessHoursDocument -> businessHoursDocument));
        int reservationDuration = 60;
        int[] capacities = {1, 2, 3, 4};
        List<TableAvailabilityDocument> tableAvailabilityDocuments = new ArrayList<>();

        for (int capacity : capacities) {
            for (MonthlyAvailabilityDocument monthlyAvailabilityDocument : monthlyAvailabilityDocuments) {
                monthlyAvailabilityDocument.getAvailableDates().forEach(date -> {
                    Day day = Day.fromDayOfWeek(LocalDate.parse(date).getDayOfWeek());
                    BusinessHoursDocument businessHoursDocument = businessHoursMap.get(day);
                    LocalTime startTime = LocalTime.parse(businessHoursDocument.getStartTime());
                    LocalTime lastTime = businessHoursDocument.getLastOrderTime() != null ? LocalTime.parse(businessHoursDocument.getLastOrderTime()) : LocalTime.parse(businessHoursDocument.getEndTime());
                    LocalTime breakStartTime = businessHoursDocument.getBreakStartTime() != null ? LocalTime.parse(businessHoursDocument.getBreakStartTime()) : null;
                    LocalTime breakEndTime = businessHoursDocument.getBreakEndTime() != null ? LocalTime.parse(businessHoursDocument.getBreakEndTime()) : null;
                    List<TimeAvailabilityDocument> timeAvailabilityDocuments = new ArrayList<>();

                    while (startTime.plusMinutes(reservationDuration).isBefore(lastTime) || startTime.plusMinutes(reservationDuration).equals(lastTime)) {
                        if (commonService.isBreakTime(startTime, breakStartTime, breakEndTime)) {
                            startTime = startTime.plusMinutes(30);
                            continue;
                        }
                        TimeAvailabilityDocument timeAvailabilityDocument = new TimeAvailabilityDocument(startTime.toString(), 0);
                        timeAvailabilityDocuments.add(timeAvailabilityDocument);
                        startTime = startTime.plusMinutes(30);
                    }

                    TableAvailabilityDocument tableAvailabilityDocument = new TableAvailabilityDocument(restaurantId, date, capacity, timeAvailabilityDocuments);
                    tableAvailabilityDocuments.add(tableAvailabilityDocument);
                });
            }
        }
        tableAvailabilityMongoRepository.saveAll(tableAvailabilityDocuments);
    }

    public List<TableAvailabilityDTO> getTableAvailability(Long restaurantId, String date, Integer capacity) {
        return tableAvailabilityMongoRepository.findByRestaurantIdAndDateAndCapacity(restaurantId, date, capacity).orElseThrow(() -> new TableAvailabilityNotFoundException(restaurantId, date, capacity))
                .getTimeAvailabilityDocuments().stream().map(TableAvailabilityDTO::new).collect(Collectors.toList());
    }
}
