package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.RestaurantEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantEventRepository extends JpaRepository<RestaurantEvent, UUID> {
}
