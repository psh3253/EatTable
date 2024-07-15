package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.MenuSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuSectionRepository extends JpaRepository<MenuSection, Long>{
    List<MenuSection> findAllByRestaurantId(Long restaurantId);
    boolean existsByRestaurantIdAndName(Long restaurantId, String name);
}
