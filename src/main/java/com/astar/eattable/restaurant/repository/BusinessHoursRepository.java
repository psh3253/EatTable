package com.astar.eattable.restaurant.repository;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.restaurant.model.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    List<BusinessHours> findAllByRestaurantId(Long restaurantId);

    Optional<BusinessHours> findByRestaurantIdAndDay(Long restaurantId, Day day);

    void deleteAllByRestaurantId(Long restaurantId);
}
