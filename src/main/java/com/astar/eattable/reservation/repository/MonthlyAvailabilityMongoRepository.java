package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.MonthlyAvailabilityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MonthlyAvailabilityMongoRepository extends MongoRepository<MonthlyAvailabilityDocument, String> {
    List<MonthlyAvailabilityDocument> findAllByRestaurantId(Long restaurantId);
}
