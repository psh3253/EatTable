package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findAllByRestaurantId(Long restaurantId);
}
