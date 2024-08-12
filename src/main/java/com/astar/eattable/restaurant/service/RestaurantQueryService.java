package com.astar.eattable.restaurant.service;

import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import com.astar.eattable.restaurant.document.RestaurantListDocument;
import com.astar.eattable.restaurant.dto.RestaurantDetailsDTO;
import com.astar.eattable.restaurant.dto.RestaurantListDTO;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.payload.*;
import com.astar.eattable.restaurant.repository.MenuRepository;
import com.astar.eattable.restaurant.repository.MenuSectionRepository;
import com.astar.eattable.restaurant.repository.RestaurantDetailsMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantListMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantQueryService {
    private final MenuSectionRepository menuSectionRepository;
    private final MenuRepository menuRepository;
    private final RestaurantListMongoRepository restaurantListMongoRepository;
    private final RestaurantDetailsMongoRepository restaurantDetailsMongoRepository;

    @Value("${restaurant.search.radius.km}")
    private double searchRadiusKm;

    public void createRestaurant(RestaurantCreateEventPayload payload) {
        restaurantListMongoRepository.save(new RestaurantListDocument(payload));
        restaurantDetailsMongoRepository.save(new RestaurantDetailsDocument(payload));
    }

    public void deleteRestaurant(RestaurantDeleteEventPayload payload) {
        restaurantListMongoRepository.deleteById(payload.getRestaurantId());
        restaurantDetailsMongoRepository.deleteById(payload.getRestaurantId());
    }

    public List<RestaurantListDTO> getNearbyRestaurants(double longitude, double latitude) {
        List<RestaurantListDocument> restaurantListDocuments = restaurantListMongoRepository.findByLocationNear(longitude, latitude, searchRadiusKm * 1000);
        return restaurantListDocuments.stream().map(RestaurantListDTO::new).toList();
    }

    public void updateRestaurant(RestaurantUpdateEventPayload payload) {
        RestaurantListDocument restaurantListDocument = restaurantListMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantListDocument.updateRestaurant(payload.getCommand());
        restaurantListMongoRepository.save(restaurantListDocument);

        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.updateRestaurant(payload.getCommand());
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public void updateBusinessHours(BusinessHoursUpdateEventPayload payload) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.updateBusinessHours(payload.getCommand());
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public RestaurantDetailsDTO getRestaurant(Long restaurantId) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        return new RestaurantDetailsDTO(restaurantDetailsDocument);
    }

    public void createMenuSection(MenuSectionCreateEventPayload payload) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.createMenuSection(payload);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public void deleteMenuSection(MenuSectionDeleteEventPayload payload) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.deleteMenuSection(payload);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public void updateMenuSection(MenuSectionUpdateEventPayload payload) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.updateMenuSection(payload);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public void createMenu(MenuCreateEventPayload payload) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.createMenu(payload);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public void deleteMenu(MenuDeleteEventPayload payload) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.deleteMenu(payload);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }

    public void updateMenu(MenuUpdateEventPayload payload) {
        RestaurantDetailsDocument restaurantDetailsDocument = restaurantDetailsMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDetailsDocument.updateMenu(payload);
        restaurantDetailsMongoRepository.save(restaurantDetailsDocument);
    }
}
