package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class RestaurantListDTO {
    private final Long id;
    private final String name;
    private final String imageUrl;
    private final String categoryName;
    private final String phone;
    private final String address;
    private final Double reviewScore;
    private final Long reviewCount;

    public RestaurantListDTO(RestaurantDocument document) {
        this.id = document.getId();
        this.name = document.getName();
        this.imageUrl = document.getImageUrl();
        this.categoryName = document.getCategoryName();
        this.phone = document.getPhone();
        this.address = document.getAddress();
        this.reviewScore = document.getReviewScore();
        this.reviewCount = document.getReviewCount();
    }
}
