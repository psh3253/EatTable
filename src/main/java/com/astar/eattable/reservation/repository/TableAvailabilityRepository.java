package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.model.TableAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TableAvailabilityRepository extends JpaRepository<TableAvailability, Long> {
    Optional<TableAvailability> findFirstByRestaurantIdOrderByDateDesc(Long restaurantId);
}
