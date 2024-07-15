package com.astar.eattable.restaurant.service;

import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import com.astar.eattable.restaurant.document.RestaurantListDocument;
import com.astar.eattable.restaurant.dto.RestaurantDetailsDTO;
import com.astar.eattable.restaurant.dto.RestaurantListDTO;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Menu;
import com.astar.eattable.restaurant.model.MenuSection;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.repository.*;
import com.astar.eattable.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantQueryService {
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final MenuSectionRepository menuSectionRepository;
    private final MenuRepository menuRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final RestaurantListMongoRepository restaurantListMongoRepository;
    private final RestaurantDetailsMongoRepository restaurantDetailsMongoRepository;

    @Value("${restaurant.search.radius.km}")
    private double searchRadiusKm;

    @Transactional(readOnly = true)
    public void createRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        List<BusinessHours> businessHoursList = businessHoursRepository.findAllByRestaurantId(restaurantId);
        restaurantListMongoRepository.save(new RestaurantListDocument(restaurant));
        restaurantDetailsMongoRepository.save(new RestaurantDetailsDocument(restaurant, businessHoursList));
    }

    public void deleteRestaurant(Long restaurantId) {
        restaurantListMongoRepository.deleteById(restaurantId);
    }

    public List<RestaurantListDTO> getNearbyRestaurants(double longitude, double latitude) {
        List<RestaurantListDocument> restaurantListDocuments = restaurantListMongoRepository.findByLocationNear(longitude, latitude, searchRadiusKm * 1000);
        return restaurantListDocuments.stream().map(RestaurantListDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public void updateRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        RestaurantListDocument restaurantListDocument = restaurantListMongoRepository.findById(restaurant.getId()).orElseThrow(() -> new RestaurantNotFoundException(restaurant.getId()));
        restaurantListDocument.updateRestaurant(restaurant);
        restaurantListMongoRepository.save(restaurantListDocument);

        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurant.getId()).orElseThrow(() -> new RestaurantNotFoundException(restaurant.getId()));
        restaurantDetailsDocument.updateRestaurant(restaurant);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    @Transactional(readOnly = true)
    public void updateBusinessHours(Long restaurantId) {
        List<BusinessHours> businessHoursList = businessHoursRepository.findAllByRestaurantId(restaurantId);

        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantDetailsDocument.updateBusinessHours(businessHoursList);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    @Transactional(readOnly = true)
    public void updateMenu(Long restaurantId) {
        List<MenuSection> menuSectionList = menuSectionRepository.findAllByRestaurantId(restaurantId);
        List<Menu> menuList = menuRepository.findAllByRestaurantId(restaurantId);
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurantDetailsDocument.updateMenuSections(menuSectionList, menuList);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public RestaurantDetailsDTO getRestaurant(Long restaurantId) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        return new RestaurantDetailsDTO(restaurantDetailsDocument);
    }
}
