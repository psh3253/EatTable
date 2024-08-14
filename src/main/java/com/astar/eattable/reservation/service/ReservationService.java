package com.astar.eattable.reservation.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.reservation.model.RestaurantTable;
import com.astar.eattable.reservation.model.TableAvailability;
import com.astar.eattable.reservation.repository.RestaurantTableRepository;
import com.astar.eattable.reservation.repository.TableAvailabilityRepository;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.repository.BusinessHoursRepository;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReservationService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantRepository restaurantRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final TableAvailabilityRepository tableAvailabilityRepository;

    @Transactional
    public void initRestaurantTable(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        int[] capacities = {1, 2, 3, 4};
        List<RestaurantTable> restaurantTables = new ArrayList<>();
        for (int capacity : capacities) {
            RestaurantTable restaurantTable = RestaurantTable.builder()
                    .capacity(capacity)
                    .count(0)
                    .restaurant(restaurant)
                    .build();
            restaurantTables.add(restaurantTable);
        }
        restaurantTableRepository.saveAll(restaurantTables);
    }

    private boolean canCreateTimeSlot(LocalTime startTime, LocalTime lastTime, int reservationDuration) {
        return startTime.plusMinutes(reservationDuration).isBefore(lastTime) || startTime.plusMinutes(reservationDuration).equals(lastTime);
    }

    @Transactional
    public void createTableAvailability(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        List<RestaurantTable> restaurantTables = restaurantTableRepository.findAllByRestaurantId(restaurantId);
        List<BusinessHours> businessHours = businessHoursRepository.findAllByRestaurantId(restaurantId);
        Map<Day, BusinessHours> businessHoursMap = businessHours.stream().collect(Collectors.toMap(BusinessHours::getDay, businessHour -> businessHour));
        List<TableAvailability> tableAvailabilities = new ArrayList<>();
        LocalDate date = LocalDate.now();

        for (int i = 0; i < 30; i++) {
            LocalTime startTime = businessHoursMap.get(Day.fromDayOfWeek(date.getDayOfWeek())).getStartTime();
            LocalTime lastTime = businessHoursMap.get(Day.fromDayOfWeek(date.getDayOfWeek())).getLastOrderTime() != null ? businessHoursMap.get(Day.fromDayOfWeek(date.getDayOfWeek())).getLastOrderTime() : businessHoursMap.get(Day.fromDayOfWeek(date.getDayOfWeek())).getEndTime();
            while (canCreateTimeSlot(startTime, lastTime, restaurant.getReservationDuration())) {
                for (RestaurantTable restaurantTable : restaurantTables) {
                    TableAvailability tableAvailability = TableAvailability.builder()
                            .date(date)
                            .startTime(startTime)
                            .endTime(startTime.plusMinutes(restaurant.getReservationDuration()))
                            .restaurant(restaurant)
                            .restaurantTable(restaurantTable)
                            .remainingTableCount(restaurantTable.getCount())
                            .build();
                    tableAvailabilities.add(tableAvailability);
                }
                startTime = startTime.plusMinutes(30);
            }
            date = date.plusDays(1);
        }
        tableAvailabilityRepository.saveAll(tableAvailabilities);
    }
}
