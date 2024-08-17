package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.RestaurantDetailsDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;
import java.util.Map;
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
    private final List<BusinessHoursDTO> businessHours;
    private final Map<Long, MenuSectionDTO> menuSections;

    public RestaurantDetailsDTO(RestaurantDetailsDocument document) {
        this.id = document.getId();
        this.name = document.getName();
        this.description = document.getDescription();
        this.imageUrl = document.getImageUrl();
        this.categoryName = document.getCategoryName();
        this.phone = document.getPhone();
        this.address = document.getAddress();
        this.reviewScore = document.getReviewScore();
        this.reviewCount = document.getReviewCount();
        this.businessHours = document.getBusinessHours().stream()
                .map(BusinessHoursDTO::new)
                .toList();
        this.menuSections = document.getMenuSections().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new MenuSectionDTO(entry.getValue())));
    }
}
