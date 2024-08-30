package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.TableAvailabilityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableAvailabilityMongoRepository extends MongoRepository<TableAvailabilityDocument, String> {
    Optional<TableAvailabilityDocument> findByRestaurantIdAndDateAndCapacity(Long restaurantId, String date, Integer capacity);

    Optional<TableAvailabilityDocument> findTopByRestaurantIdOrderByDateDesc(Long restaurantId);

    List<TableAvailabilityDocument> findAllByRestaurantIdAndCapacity(Long restaurantId, Integer capacity);
}
