package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.restaurant.payload.RestaurantCreateEventPayload;
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

    public RestaurantListDocument(RestaurantCreateEventPayload payload) {
        this.id = payload.getRestaurantId();
        this.name = payload.getCommand().getName();
        this.imageUrl = payload.getCommand().getImageUrl();
        this.categoryName = payload.getCommand().getCategoryName();
        this.phone = payload.getCommand().getPhone();
        this.address = payload.getCommand().getAddress();
        this.location = new GeoJsonPoint(payload.getCommand().getLongitude(), payload.getCommand().getLatitude());
    }

    public void updateRestaurant(RestaurantUpdateCommand command) {
        if (command.getName() != null)
            this.name = command.getName();
        if (command.getImageUrl() != null)
            this.imageUrl = command.getImageUrl();
        if (command.getCategoryName() != null)
            this.categoryName = command.getCategoryName();
        if (command.getPhone() != null)
            this.phone = command.getPhone();
        if (command.getAddress() != null)
            this.address = command.getAddress();
        if (command.getLongitude() != null && command.getLatitude() != null)
            this.location = new GeoJsonPoint(command.getLongitude(), command.getLatitude());
    }
}
