package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.document.MenuSectionMapDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MenuSectionMapRepository extends MongoRepository<MenuSectionMapDocument, Long> {
    Optional<MenuSectionMapDocument> findByRestaurantId(Long restaurantId);
}
