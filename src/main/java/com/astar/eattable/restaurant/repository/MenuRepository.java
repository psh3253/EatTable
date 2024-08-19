package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByRestaurantId(Long restaurantId);

    boolean existsByMenuSectionId(Long menuSectionId);

    boolean existsByMenuSectionIdAndName(Long menuSectionId, String name);
}
