package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantDetailsMongoRepository extends MongoRepository<RestaurantDetailsDocument, Long> {
}
