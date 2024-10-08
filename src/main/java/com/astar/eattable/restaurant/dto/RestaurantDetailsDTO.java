package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;

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
    private final Long userId;
    private final String userNickname;
    private final List<BusinessHoursDTO> businessHours;

    public RestaurantDetailsDTO(RestaurantDocument document) {
        this.id = document.getId();
        this.name = document.getName();
        this.description = document.getDescription();
        this.imageUrl = document.getImageUrl();
        this.categoryName = document.getCategoryName();
        this.phone = document.getPhone();
        this.address = document.getAddress();
        this.reviewScore = document.getReviewScore();
        this.reviewCount = document.getReviewCount();
        this.userId = document.getUserId();
        this.userNickname = document.getUserNickname();
        this.businessHours = document.getBusinessHours().stream()
                .map(BusinessHoursDTO::new)
                .toList();
    }
}
