package com.astar.eattable.restaurant.controller;

import com.astar.eattable.restaurant.command.*;
import com.astar.eattable.restaurant.dto.RestaurantDetailsDTO;
import com.astar.eattable.restaurant.dto.RestaurantListDTO;
import com.astar.eattable.restaurant.service.RestaurantCommandService;
import com.astar.eattable.restaurant.service.RestaurantQueryService;
import com.astar.eattable.security.CurrentUser;
import com.astar.eattable.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
@RestController
public class RestaurantController {
    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantCommandService restaurantCommandService;

    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantListDTO>> getNearbyRestaurants(@RequestParam double longitude, @RequestParam double latitude) {
        return ResponseEntity.ok(restaurantQueryService.getNearbyRestaurants(longitude, latitude));
    }

    @PostMapping
    public ResponseEntity<Long> createRestaurant(@Valid @RequestBody RestaurantCreateCommand command, @CurrentUser User currentUser) throws JsonProcessingException {
        Long restaurantId = restaurantCommandService.createRestaurant(command, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantId);
    }

    @DeleteMapping("/{restaurantId}")
    public void deleteRestaurant(@PathVariable Long restaurantId) {
        restaurantCommandService.deleteRestaurant(restaurantId);
    }

    @PutMapping("/{restaurantId}")
    public void updateRestaurant(@PathVariable Long restaurantId, @Valid @RequestBody RestaurantUpdateCommand command, @CurrentUser User currentUser) throws JsonProcessingException {
        restaurantCommandService.updateRestaurant(restaurantId, command, currentUser);
    }

    @PutMapping("/{restaurantId}/business-hours")
    public void updateBusinessHours(@PathVariable Long restaurantId, @Valid @RequestBody BusinessHoursUpdateCommand command, @CurrentUser User currentUser) throws JsonProcessingException {
        restaurantCommandService.updateBusinessHours(restaurantId, command, currentUser);
    }

    @PostMapping("/{restaurantId}/menu-sections")
    public void createMenuSection(@PathVariable Long restaurantId, @Valid @RequestBody MenuSectionCreateCommand command, @CurrentUser User currentUser) throws JsonProcessingException {
        restaurantCommandService.createMenuSection(restaurantId, command, currentUser);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailsDTO> getRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantQueryService.getRestaurant(restaurantId));
    }

    @PutMapping("/{restaurantId}/menu-sections/{menuSectionId}")
    public void updateMenuSection(@PathVariable Long restaurantId, @PathVariable Long menuSectionId, @Valid @RequestBody MenuSectionUpdateCommand command, @CurrentUser User currentUser) throws JsonProcessingException {
        restaurantCommandService.updateMenuSection(restaurantId, menuSectionId, command, currentUser);
    }
}
