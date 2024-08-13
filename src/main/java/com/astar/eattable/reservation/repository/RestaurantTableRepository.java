package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

}
