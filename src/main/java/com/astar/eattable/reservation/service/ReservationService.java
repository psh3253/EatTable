package com.astar.eattable.reservation.service;

import com.astar.eattable.reservation.model.RestaurantTable;
import com.astar.eattable.reservation.repository.RestaurantTableRepository;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantRepository restaurantRepository;

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
}
