package com.astar.eattable.restaurant.service;

import com.astar.eattable.restaurant.document.RestaurantListDocument;
import com.astar.eattable.restaurant.dto.RestaurantListDto;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.repository.RestaurantMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
import com.astar.eattable.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestaurantQueryService {
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantMongoRepository restaurantMongoRepository;

    @Value("${restaurant.search.radius.km}")
    private double searchRadiusKm;

    @Transactional
    public void createRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        Double avgScore = Optional.ofNullable(reviewRepository.getAvgScoreByRestaurantId(restaurantId)).orElse(0.0);
        Long reviewCount = reviewRepository.countByRestaurantId(restaurantId);
        restaurantMongoRepository.save(new RestaurantListDocument(restaurant, avgScore, reviewCount));
    }

    public void deleteRestaurant(Long restaurantId) {
        restaurantMongoRepository.deleteById(restaurantId);
    }

    public List<RestaurantListDto> getNearbyRestaurants(double longitude, double latitude) {
        List<RestaurantListDocument> restaurantListDocuments = restaurantMongoRepository.findByLocationNear(longitude, latitude, searchRadiusKm * 1000);
        return restaurantListDocuments.stream().map(RestaurantListDto::new).toList();
    }

    public void updateRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        RestaurantListDocument restaurantListDocument = restaurantMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantListDocument.updateRestaurant(restaurant);
        restaurantMongoRepository.save(restaurantListDocument);
    }
}
