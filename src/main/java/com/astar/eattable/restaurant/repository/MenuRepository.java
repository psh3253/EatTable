package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByRestaurantId(Long restaurantId);
}
