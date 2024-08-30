package com.astar.eattable.restaurant.repository;

import com.astar.eattable.restaurant.document.ClosedPeriodDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClosedPeriodMongoRepository extends MongoRepository<ClosedPeriodDocument, Long> {
    List<ClosedPeriodDocument> findAllByRestaurantId(Long restaurantId);

    @Query("{ 'restaurantId' : ?0, 'startDate' : { $lte : ?1 }, 'endDate' : { $gte : ?1 } }")
    Optional<ClosedPeriodDocument> findClosedPeriod(Long restaurantId, String date);
}
