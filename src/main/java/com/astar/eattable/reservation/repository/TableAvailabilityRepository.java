package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.model.TableAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TableAvailabilityRepository extends JpaRepository<TableAvailability, Long> {
    Optional<TableAvailability> findFirstByRestaurantIdOrderByDateDesc(Long restaurantId);

    List<TableAvailability> findAllByRestaurantIdAndRestaurantTableCapacity(Long restaurantId, Integer capacity);

    Optional<TableAvailability> findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(Long restaurantId, LocalDate date, LocalTime startTime, Integer capacity);
}
