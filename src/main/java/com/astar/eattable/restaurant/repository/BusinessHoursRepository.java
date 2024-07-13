package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.dto.Day;
import com.astar.eattable.restaurant.model.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    List<BusinessHours> findAllByRestaurantId(Long restaurantId);

    Optional<BusinessHours> findByRestaurantIdAndDay(Long restaurantId, Day day);

    void deleteAllByRestaurantId(Long restaurantId);
}
