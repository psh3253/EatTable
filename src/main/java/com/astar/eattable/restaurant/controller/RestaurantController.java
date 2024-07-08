package com.astar.eattable.restaurant.controller;

import com.astar.eattable.restaurant.command.RestaurantCreateCommand;
import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.restaurant.dto.RestaurantListDto;
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
    public ResponseEntity<List<RestaurantListDto>> getNearbyRestaurants(@RequestParam double longitude, @RequestParam double latitude) {
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
}
