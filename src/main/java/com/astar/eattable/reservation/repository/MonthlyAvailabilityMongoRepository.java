package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.MonthlyAvailabilityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyAvailabilityMongoRepository extends MongoRepository<MonthlyAvailabilityDocument, String> {
    List<MonthlyAvailabilityDocument> findAllByRestaurantId(Long restaurantId);
}
