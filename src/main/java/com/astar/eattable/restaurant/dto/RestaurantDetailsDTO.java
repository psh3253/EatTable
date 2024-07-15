package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.MenuSectionDocument;
import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class RestaurantDetailsDTO {
    private final Long id;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final String categoryName;
    private final String phone;
    private final String address;
    private final Double reviewScore;
    private final Long reviewCount;
    private final List<MenuSectionDTO> menuSections;

    public RestaurantDetailsDTO(RestaurantDetailsDocument restaurantDetailsDocument) {
        this.id = restaurantDetailsDocument.getId();
        this.name = restaurantDetailsDocument.getName();
        this.description = restaurantDetailsDocument.getDescription();
        this.imageUrl = restaurantDetailsDocument.getImageUrl();
        this.categoryName = restaurantDetailsDocument.getCategoryName();
        this.phone = restaurantDetailsDocument.getPhone();
        this.address = restaurantDetailsDocument.getAddress();
        this.reviewScore = restaurantDetailsDocument.getReviewScore();
        this.reviewCount = restaurantDetailsDocument.getReviewCount();
        this.menuSections = restaurantDetailsDocument.getMenuSections().stream()
                .map(MenuSectionDTO::new)
                .toList();
    }
}
