package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByNameAndAddress(String name, String address);
}
