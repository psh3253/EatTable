package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class RestaurantListDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String categoryName;
    private String phone;
    private String address;
    private Double reviewScore;
    private Long reviewCount;

    public RestaurantListDto(RestaurantDocument restaurantDocument) {
        this.id = restaurantDocument.getId();
        this.name = restaurantDocument.getName();
        this.imageUrl = restaurantDocument.getImageUrl();
        this.categoryName = restaurantDocument.getCategoryName();
        this.phone = restaurantDocument.getPhone();
        this.address = restaurantDocument.getAddress();
        this.reviewScore = restaurantDocument.getReviewScore();
        this.reviewCount = restaurantDocument.getReviewCount();
    }
}
