package com.astar.eattable.restaurant.service;

import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import com.astar.eattable.restaurant.document.RestaurantListDocument;
import com.astar.eattable.restaurant.dto.RestaurantListDto;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.repository.BusinessHoursRepository;
import com.astar.eattable.restaurant.repository.RestaurantDetailsMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantListMongoRepository;
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
    private final BusinessHoursRepository businessHoursRepository;
    private final RestaurantListMongoRepository restaurantListMongoRepository;
    private final RestaurantDetailsMongoRepository restaurantDetailsMongoRepository;

    @Value("${restaurant.search.radius.km}")
    private double searchRadiusKm;

    @Transactional
    public void createRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        Double avgScore = Optional.ofNullable(reviewRepository.getAvgScoreByRestaurantId(restaurantId)).orElse(0.0);
        Long reviewCount = reviewRepository.countByRestaurantId(restaurantId);
        List<BusinessHours> businessHoursList = businessHoursRepository.findAllByRestaurantId(restaurantId);
        restaurantListMongoRepository.save(new RestaurantListDocument(restaurant, avgScore, reviewCount));
        restaurantDetailsMongoRepository.save(new RestaurantDetailsDocument(restaurant, avgScore, reviewCount, businessHoursList));
    }

    public void deleteRestaurant(Long restaurantId) {
        restaurantListMongoRepository.deleteById(restaurantId);
    }

    public List<RestaurantListDto> getNearbyRestaurants(double longitude, double latitude) {
        List<RestaurantListDocument> restaurantListDocuments = restaurantListMongoRepository.findByLocationNear(longitude, latitude, searchRadiusKm * 1000);
        return restaurantListDocuments.stream().map(RestaurantListDto::new).toList();
    }

    public void updateRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        RestaurantListDocument restaurantListDocument = restaurantListMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantListDocument.updateRestaurant(restaurant);
        restaurantListMongoRepository.save(restaurantListDocument);

        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantDetailsDocument.updateRestaurant(restaurant);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public void updateBusinessHours(Long restaurantId) {
        List<BusinessHours> businessHoursList = businessHoursRepository.findAllByRestaurantId(restaurantId);

        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantDetailsDocument.updateBusinessHours(businessHoursList);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }
}
