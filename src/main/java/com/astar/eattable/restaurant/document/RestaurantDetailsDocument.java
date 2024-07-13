package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Restaurant;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "restaurant_details")
public class RestaurantDetailsDocument {
    @Id
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String categoryName;
    private String phone;
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private Double reviewScore;
    private Long reviewCount;

    List<BusinessHoursDocument> businessHours;

    public RestaurantDetailsDocument(Restaurant restaurant, Double reviewScore, Long reviewCount, List<BusinessHours> businessHoursList) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.description = restaurant.getDescription();
        this.imageUrl = restaurant.getImageUrl();
        this.categoryName = restaurant.getCategoryName();
        this.phone = restaurant.getPhone();
        this.address = restaurant.getAddress();
        this.location = new GeoJsonPoint(restaurant.getLongitude(), restaurant.getLatitude());
        this.reviewScore = reviewScore;
        this.reviewCount = reviewCount;
        this.businessHours = businessHoursList.stream().map(BusinessHoursDocument::new).collect(Collectors.toList());
    }

    public void updateRestaurant(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.description = restaurant.getDescription();
        this.imageUrl = restaurant.getImageUrl();
        this.categoryName = restaurant.getCategoryName();
        this.phone = restaurant.getPhone();
        this.address = restaurant.getAddress();
        this.location = new GeoJsonPoint(restaurant.getLongitude(), restaurant.getLatitude());
    }

    public void updateBusinessHours(List<BusinessHours> businessHoursList) {
        this.businessHours = businessHoursList.stream().map(BusinessHoursDocument::new).collect(Collectors.toList());
    }
}
