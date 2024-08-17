package com.astar.eattable.restaurant.controller;

import com.astar.eattable.restaurant.command.*;
import com.astar.eattable.restaurant.dto.ClosedPeriodListDTO;
import com.astar.eattable.restaurant.dto.MenuSectionDTO;
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
import java.util.Map;

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
    public ResponseEntity<Long> createRestaurant(@Valid @RequestBody RestaurantCreateCommand command, @CurrentUser User currentUser) {
        Long restaurantId = restaurantCommandService.createRestaurant(command, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantId);
    }

    @DeleteMapping("/{restaurantId}")
    public void deleteRestaurant(@PathVariable Long restaurantId, @CurrentUser User currentUser) {
        restaurantCommandService.deleteRestaurant(restaurantId, currentUser);
    }

    @PutMapping("/{restaurantId}")
    public void updateRestaurant(@PathVariable Long restaurantId, @Valid @RequestBody RestaurantUpdateCommand command, @CurrentUser User currentUser) {
        restaurantCommandService.updateRestaurant(restaurantId, command, currentUser);
    }

    @PutMapping("/{restaurantId}/business-hours")
    public void updateBusinessHours(@PathVariable Long restaurantId, @Valid @RequestBody BusinessHoursUpdateCommand command, @CurrentUser User currentUser) {
        restaurantCommandService.updateBusinessHours(restaurantId, command, currentUser);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailsDTO> getRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantQueryService.getRestaurant(restaurantId));
    }

    @PostMapping("/{restaurantId}/menu-sections")
    public void createMenuSection(@PathVariable Long restaurantId, @Valid @RequestBody MenuSectionCreateCommand command, @CurrentUser User currentUser) {
        restaurantCommandService.createMenuSection(restaurantId, command, currentUser);
    }

    @PutMapping("/{restaurantId}/menu-sections/{menuSectionId}")
    public void updateMenuSection(@PathVariable String restaurantId, @PathVariable Long menuSectionId, @Valid @RequestBody MenuSectionUpdateCommand command, @CurrentUser User currentUser) {
        restaurantCommandService.updateMenuSection(menuSectionId, command, currentUser);
    }

    @DeleteMapping("/{restaurantId}/menu-sections/{menuSectionId}")
    public void deleteMenuSection(@PathVariable String restaurantId, @PathVariable Long menuSectionId, @CurrentUser User currentUser) {
        restaurantCommandService.deleteMenuSection(menuSectionId, currentUser);
    }

    @PostMapping("/{restaurantId}/menu-sections/{menuSectionId}/menus")
    public void createMenu(@PathVariable Long restaurantId, @PathVariable Long menuSectionId, @Valid @RequestBody MenuCreateCommand command, @CurrentUser User currentUser) {
        restaurantCommandService.createMenu(menuSectionId, command, currentUser);
    }

    @DeleteMapping("/{restaurantId}/menu-sections/{menuSectionId}/menus/{menuId}")
    public void deleteMenu(@PathVariable Long restaurantId, @PathVariable Long menuSectionId, @PathVariable Long menuId, @CurrentUser User currentUser) {
        restaurantCommandService.deleteMenu(menuId, currentUser);
    }

    @PutMapping("/{restaurantId}/menu-sections/{menuSectionId}/menus/{menuId}")
    public void updateMenu(@PathVariable Long restaurantId, @PathVariable Long menuSectionId, @PathVariable Long menuId, @Valid @RequestBody MenuUpdateCommand command, @CurrentUser User currentUser) throws JsonProcessingException {
        restaurantCommandService.updateMenu(menuId, command, currentUser);
    }

    @GetMapping("/{restaurantId}/menu-sections")
    public ResponseEntity<Map<Long, MenuSectionDTO>> getMenuSections(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantQueryService.getMenuSections(restaurantId));
    }

    @PostMapping("/{restaurantId}/closed-periods")
    public void createClosedPeriod(@PathVariable Long restaurantId, @Valid @RequestBody ClosedPeriodCreateCommand command, @CurrentUser User currentUser) {
        restaurantCommandService.createClosedPeriod(restaurantId, command, currentUser);
    }

    @DeleteMapping("/{restaurantId}/closed-periods/{closedPeriodId}")
    public void deleteClosedPeriod(@PathVariable Long restaurantId, @PathVariable Long closedPeriodId, @CurrentUser User currentUser) {
        restaurantCommandService.deleteClosedPeriod(closedPeriodId, currentUser);
    }

    @GetMapping("/{restaurantId}/closed-periods")
    public ResponseEntity<List<ClosedPeriodListDTO>> getClosedPeriods(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantQueryService.getClosedPeriods(restaurantId));
    }
}
