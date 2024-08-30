package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.document.MenuSectionMapDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuSectionMapMongoRepository extends MongoRepository<MenuSectionMapDocument, Long> {
    Optional<MenuSectionMapDocument> findByRestaurantId(Long restaurantId);
}
