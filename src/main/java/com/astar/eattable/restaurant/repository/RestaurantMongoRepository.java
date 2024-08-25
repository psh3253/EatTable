package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.document.RestaurantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RestaurantMongoRepository extends MongoRepository<RestaurantDocument, Long> {
    @Query("{ location: { $near: { $geometry: { type: 'Point', coordinates: [ ?0, ?1 ] }, $maxDistance: ?2 } } }")
    List<RestaurantDocument> findByLocationNear(double longitude, double latitude, double maxDistance);
}
