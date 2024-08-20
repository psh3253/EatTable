package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.ReservationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationMongoRepository extends MongoRepository<ReservationDocument, Long> {
    Integer countAllByRestaurantIdAndDateAndTimeAndCapacity(Long restaurantId, String date, String time, Integer capacity);
}
