package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.document.ClosedPeriodDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClosedPeriodMongoRepository extends MongoRepository<ClosedPeriodDocument, Long> {
    List<ClosedPeriodDocument> findAllByRestaurantId(Long restaurantId);
}
