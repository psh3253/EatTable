package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.MenuSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuSectionRepository extends JpaRepository<MenuSection, Long> {
    boolean existsByRestaurantIdAndName(Long restaurantId, String name);
}
