package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.ClosedPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClosedPeriodRepository extends JpaRepository<ClosedPeriod, Long> {
    List<ClosedPeriod> findAllByRestaurantId(Long restaurantId);

    @Query("SELECT cp FROM ClosedPeriod cp WHERE cp.restaurant.id = ?1 AND cp.startDate <= ?2 AND cp.endDate >= ?2")
    Optional<ClosedPeriod> findClosedPeriod(Long restaurantId, String date);
}
