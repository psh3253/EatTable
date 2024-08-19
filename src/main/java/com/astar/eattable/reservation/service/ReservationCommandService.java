package com.astar.eattable.reservation.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.common.service.CommonService;
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
public class ReservationCommandService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantRepository restaurantRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final TableAvailabilityRepository tableAvailabilityRepository;
    private final CommonService commonService;

    @Transactional
    public void initRestaurantTable(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        int[] capacities = {1, 2, 3, 4};
        List<RestaurantTable> restaurantTables = new ArrayList<>();
        for (int capacity : capacities) {
            RestaurantTable restaurantTable = RestaurantTable.builder().capacity(capacity).count(0).restaurant(restaurant).build();
            restaurantTables.add(restaurantTable);
        }
        restaurantTableRepository.saveAll(restaurantTables);
    }

    @Transactional
    public void createTableAvailabilities(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        List<RestaurantTable> restaurantTables = restaurantTableRepository.findAllByRestaurantId(restaurantId);
        List<BusinessHours> businessHours = businessHoursRepository.findAllByRestaurantId(restaurantId);
        Map<Day, BusinessHours> businessHoursMap = businessHours.stream().collect(Collectors.toMap(BusinessHours::getDay, businessHour -> businessHour));
        List<TableAvailability> tableAvailabilities = new ArrayList<>();
        LocalDate endDate = LocalDate.now().plusDays(31);
        LocalDate currentDate = getTableAvailabilityLastDateNextDay(restaurantId);
        Integer reservationDuration = restaurant.getReservationDuration();
        LocalTime startTime = businessHoursMap.get(Day.fromDayOfWeek(currentDate.getDayOfWeek())).getStartTime();
        LocalTime lastTime = businessHoursMap.get(Day.fromDayOfWeek(currentDate.getDayOfWeek())).getLastOrderTime() != null ? businessHoursMap.get(Day.fromDayOfWeek(currentDate.getDayOfWeek())).getLastOrderTime() : businessHoursMap.get(Day.fromDayOfWeek(currentDate.getDayOfWeek())).getEndTime();
        LocalTime breakStartTime = businessHoursMap.get(Day.fromDayOfWeek(currentDate.getDayOfWeek())).getBreakStartTime();
        LocalTime breakEndTime = businessHoursMap.get(Day.fromDayOfWeek(currentDate.getDayOfWeek())).getBreakEndTime();

        while (currentDate.isBefore(endDate)) {
            List<TableAvailability> dayTableAvailabilities = createDayTableAvailabilities(startTime, lastTime, breakStartTime, breakEndTime, reservationDuration, restaurantTables, currentDate, restaurant);
            currentDate = currentDate.plusDays(1);
            tableAvailabilities.addAll(dayTableAvailabilities);
        }
        tableAvailabilityRepository.saveAll(tableAvailabilities);
    }

    private List<TableAvailability> createDayTableAvailabilities(LocalTime startTime, LocalTime lastTime, LocalTime breakStartTime, LocalTime breakEndTime, Integer reservationDuration, List<RestaurantTable> restaurantTables, LocalDate date, Restaurant restaurant) {
        List<TableAvailability> dayTableAvailabilities = new ArrayList<>();
        while (startTime.plusMinutes(reservationDuration).isBefore(lastTime) || startTime.plusMinutes(reservationDuration).equals(lastTime)) {
            if (commonService.isBreakTime(startTime, breakStartTime, breakEndTime)) {
                startTime = startTime.plusMinutes(30);
                continue;
            }
            for (RestaurantTable restaurantTable : restaurantTables) {
                TableAvailability tableAvailability = TableAvailability.builder().date(date).startTime(startTime).endTime(startTime.plusMinutes(reservationDuration)).restaurant(restaurant).restaurantTable(restaurantTable).remainingTableCount(restaurantTable.getCount()).build();
                dayTableAvailabilities.add(tableAvailability);
            }
            startTime = startTime.plusMinutes(30);
        }
        return dayTableAvailabilities;
    }

    @Transactional(readOnly = true)
    public LocalDate getTableAvailabilityLastDateNextDay(Long restaurantId) {
        return tableAvailabilityRepository.findFirstByRestaurantIdOrderByDateDesc(restaurantId).map(TableAvailability::getDate).orElse(LocalDate.now().minusDays(1)).plusDays(1);
    }

    @Transactional
    public void createAllTableAvailabilities() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        for (Restaurant restaurant : restaurants) {
            createTableAvailabilities(restaurant.getId());
        }
    }
}
