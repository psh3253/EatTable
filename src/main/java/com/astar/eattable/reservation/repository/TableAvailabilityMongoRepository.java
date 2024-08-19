package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.TableAvailabilityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TableAvailabilityMongoRepository extends MongoRepository<TableAvailabilityDocument, String> {
    Optional<TableAvailabilityDocument> findByRestaurantIdAndDateAndCapacity(Long restaurantId, String date, Integer capacity);
}
