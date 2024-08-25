package com.astar.eattable.restaurant.service;

import com.astar.eattable.restaurant.document.ClosedPeriodDocument;
import com.astar.eattable.restaurant.document.MenuSectionMapDocument;
import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.astar.eattable.restaurant.dto.ClosedPeriodListDTO;
import com.astar.eattable.restaurant.dto.MenuSectionDTO;
import com.astar.eattable.restaurant.dto.RestaurantDetailsDTO;
import com.astar.eattable.restaurant.dto.RestaurantListDTO;
import com.astar.eattable.restaurant.exception.MenuSectionMapNotFoundException;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.payload.*;
import com.astar.eattable.restaurant.repository.ClosedPeriodMongoRepository;
import com.astar.eattable.restaurant.repository.MenuSectionMapMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RestaurantQueryService {
    private final RestaurantMongoRepository restaurantMongoRepository;
    private final ClosedPeriodMongoRepository closedPeriodMongoRepository;
    private final MenuSectionMapMongoRepository menuSectionMapMongoRepository;

    @Value("${restaurant.search.radius.km}")
    private double searchRadiusKm;

    public void createRestaurant(RestaurantCreateEventPayload payload) {
        restaurantMongoRepository.save(new RestaurantDocument(payload));
    }

    public void deleteRestaurant(RestaurantDeleteEventPayload payload) {
        restaurantMongoRepository.deleteById(payload.getRestaurantId());
    }

    public List<RestaurantListDTO> getNearbyRestaurants(double longitude, double latitude) {
        List<RestaurantDocument> restaurantListDocuments = restaurantMongoRepository.findByLocationNear(longitude, latitude, searchRadiusKm * 1000);
        return restaurantListDocuments.stream().map(RestaurantListDTO::new).toList();
    }

    public void updateRestaurant(RestaurantUpdateEventPayload payload) {
        RestaurantDocument restaurantDocument = restaurantMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDocument.updateRestaurant(payload.getCommand());
        restaurantMongoRepository.save(restaurantDocument);
    }

    public void updateBusinessHours(BusinessHoursUpdateEventPayload payload) {
        RestaurantDocument restaurantDocument = restaurantMongoRepository.findById(payload.getRestaurantId()).orElseThrow(() -> new RestaurantNotFoundException(payload.getRestaurantId()));
        restaurantDocument.updateBusinessHours(payload.getCommand());
        restaurantMongoRepository.save(restaurantDocument);
    }

    public RestaurantDetailsDTO getRestaurant(Long restaurantId) {
        RestaurantDocument restaurantDocument = restaurantMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        return new RestaurantDetailsDTO(restaurantDocument);
    }

    public void initMenuSections(Long restaurantId) {
        MenuSectionMapDocument menuSectionMapDocument = new MenuSectionMapDocument(restaurantId);
        menuSectionMapMongoRepository.save(menuSectionMapDocument);
    }

    public void createMenuSection(MenuSectionCreateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapMongoRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.createMenuSection(payload);
        menuSectionMapMongoRepository.save(menuSectionMapDocument);
    }

    public void deleteMenuSection(MenuSectionDeleteEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapMongoRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.deleteMenuSection(payload);
        menuSectionMapMongoRepository.save(menuSectionMapDocument);
    }

    public void updateMenuSection(MenuSectionUpdateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapMongoRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.updateMenuSection(payload);
        menuSectionMapMongoRepository.save(menuSectionMapDocument);
    }

    public void createMenu(MenuCreateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapMongoRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.createMenu(payload);
        menuSectionMapMongoRepository.save(menuSectionMapDocument);
    }

    public void deleteMenu(MenuDeleteEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapMongoRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.deleteMenu(payload);
        menuSectionMapMongoRepository.save(menuSectionMapDocument);
    }

    public void updateMenu(MenuUpdateEventPayload payload) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapMongoRepository.findByRestaurantId(payload.getRestaurantId()).orElseThrow(() -> new MenuSectionMapNotFoundException(payload.getRestaurantId()));
        menuSectionMapDocument.updateMenu(payload);
        menuSectionMapMongoRepository.save(menuSectionMapDocument);
    }

    public Map<Long, MenuSectionDTO> getMenuSections(Long restaurantId) {
        MenuSectionMapDocument menuSectionMapDocument = menuSectionMapMongoRepository.findByRestaurantId(restaurantId).orElseThrow(() -> new MenuSectionMapNotFoundException(restaurantId));
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
