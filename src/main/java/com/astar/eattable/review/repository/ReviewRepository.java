package com.astar.eattable.review.repository;

import com.astar.eattable.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT AVG(r.score) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double getAvgScoreByRestaurantId(@Param("restaurantId") Long restaurantId);

    Long countByRestaurantId(Long restaurantId);
}
