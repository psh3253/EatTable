package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.ReservationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationMongoRepository extends MongoRepository<ReservationDocument, Long>, ReservationMongoRepositoryCustom {
    Integer countAllByRestaurantIdAndDateAndTimeAndCapacity(Long restaurantId, String date, String time, Integer capacity);

    List<ReservationDocument> findAllByUserId(Long userId);

    Optional<ReservationDocument> findById(Long reservationId);
}
