package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.model.Restaurant;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "restaurant_list")
public class RestaurantListDocument {
    @Id
    private Long id;
    private String name;
    private String imageUrl;
    private String categoryName;
    private String phone;
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private Double reviewScore = 0.0;
    private Long reviewCount = 0L;

    public RestaurantListDocument(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.imageUrl = restaurant.getImageUrl();
        this.categoryName = restaurant.getCategoryName();
        this.phone = restaurant.getPhone();
        this.address = restaurant.getAddress();
        this.location = new GeoJsonPoint(restaurant.getLongitude(), restaurant.getLatitude());
    }

    public void updateRestaurant(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.imageUrl = restaurant.getImageUrl();
        this.categoryName = restaurant.getCategoryName();
        this.phone = restaurant.getPhone();
        this.address = restaurant.getAddress();
        this.location = new GeoJsonPoint(restaurant.getLongitude(), restaurant.getLatitude());
    }
}
