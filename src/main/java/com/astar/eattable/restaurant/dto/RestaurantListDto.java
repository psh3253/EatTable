package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.RestaurantListDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class RestaurantListDto {
    private final Long id;
    private final String name;
    private final String imageUrl;
    private final String categoryName;
    private final String phone;
    private final String address;
    private final Double reviewScore;
    private final Long reviewCount;

    public RestaurantListDto(RestaurantListDocument restaurantListDocument) {
        this.id = restaurantListDocument.getId();
        this.name = restaurantListDocument.getName();
        this.imageUrl = restaurantListDocument.getImageUrl();
        this.categoryName = restaurantListDocument.getCategoryName();
        this.phone = restaurantListDocument.getPhone();
        this.address = restaurantListDocument.getAddress();
        this.reviewScore = restaurantListDocument.getReviewScore();
        this.reviewCount = restaurantListDocument.getReviewCount();
    }
}
