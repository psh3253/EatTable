package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.ClosedPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosedPeriodRepository extends JpaRepository<ClosedPeriod, Long> {
    List<ClosedPeriod> findAllByRestaurantId(Long restaurantId);
}
