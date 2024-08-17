package com.astar.eattable.restaurant.service;

import com.astar.eattable.restaurant.document.ClosedPeriodDocument;
import com.astar.eattable.restaurant.document.MenuSectionMapDocument;
import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import com.astar.eattable.restaurant.document.RestaurantListDocument;
import com.astar.eattable.restaurant.dto.ClosedPeriodListDTO;
import com.astar.eattable.restaurant.dto.MenuSectionDTO;
import com.astar.eattable.restaurant.dto.RestaurantDetailsDTO;
import com.astar.eattable.restaurant.dto.RestaurantListDTO;
import com.astar.eattable.restaurant.exception.MenuSectionMapNotFoundException;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.payload.*;
import com.astar.eattable.restaurant.repository.ClosedPeriodMongoRepository;
import com.astar.eattable.restaurant.repository.MenuSectionMapRepository;
import com.astar.eattable.restaurant.repository.RestaurantDetailsMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantListMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RestaurantQueryService {
    private final RestaurantListMongoRepository restaurantListMongoRepository;
    private final RestaurantDetailsMongoRepository restaurantDetailsMongoRepository;
    private final ClosedPeriodMongoRepository closedPeriodMongoRepository;
    private final MenuSectionMapRepository menuSectionMapRepository;

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

    public void initMenuSections(Long restaurantId) {
        MenuSectionMapDocument menuSectionMapDocument = new MenuSectionMapDocument(restaurantId);
        menuSectionMapRepository.save(menuSectionMapDocument);
    }

    public void createMenuSection(MenuSectionCreateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.createMenuSection(payload);
        menuSectionMapRepository.save(menuSectionMapDocument);
    }

    public void deleteMenuSection(MenuSectionDeleteEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.deleteMenuSection(payload);
        menuSectionMapRepository.save(menuSectionMapDocument);
    }

    public void updateMenuSection(MenuSectionUpdateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.updateMenuSection(payload);
        menuSectionMapRepository.save(menuSectionMapDocument);
    }

    public void createMenu(MenuCreateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.createMenu(payload);
        menuSectionMapRepository.save(menuSectionMapDocument);
    }

    public void deleteMenu(MenuDeleteEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.deleteMenu(payload);
        menuSectionMapRepository.save(menuSectionMapDocument);
    }

    public void updateMenu(MenuUpdateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.updateMenu(payload);
        menuSectionMapRepository.save(menuSectionMapDocument);
    }

    public Map<Long, MenuSectionDTO> getMenuSections(Long restaurantId) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapRepository.findByRestaurantId(restaurantId).orElseThrow(() -> new MenuSectionMapNotFoundException(restaurantId));
        return menuSectionMapDocument.getMenuSections().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new MenuSectionDTO(entry.getValue())));
    }

    public void createClosedPeriod(ClosedPeriodCreateEventPayload payload) {
        ClosedPeriodDocument closedPeriodDocument = new ClosedPeriodDocument(payload);
        closedPeriodMongoRepository.save(closedPeriodDocument);
    }

    public void deleteClosedPeriod(ClosedPeriodDeleteEventPayload payload) {
        closedPeriodMongoRepository.deleteById(payload.getClosedPeriodId());
    }

    public List<ClosedPeriodListDTO> getClosedPeriods(Long restaurantId) {
        List<ClosedPeriodDocument> closedPeriodDocuments = closedPeriodMongoRepository.findAllByRestaurantId(restaurantId);
        return closedPeriodDocuments.stream().map(ClosedPeriodListDTO::new).toList();
    }
}
