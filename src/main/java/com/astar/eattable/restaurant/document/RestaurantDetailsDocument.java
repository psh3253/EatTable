package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.restaurant.payload.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Double reviewScore = 0.0;
    private Long reviewCount = 0L;
    List<BusinessHoursDocument> businessHours;
    Map<Long, MenuSectionDocument> menuSections;

    public RestaurantDetailsDocument(RestaurantCreateEventPayload payload) {
        this.id = payload.getRestaurantId();
        this.name = payload.getCommand().getName();
        this.description = payload.getCommand().getDescription();
        this.imageUrl = payload.getCommand().getImageUrl();
        this.categoryName = payload.getCommand().getCategoryName();
        this.phone = payload.getCommand().getPhone();
        this.address = payload.getCommand().getAddress();
        this.location = new GeoJsonPoint(payload.getCommand().getLongitude(), payload.getCommand().getLatitude());
        this.businessHours = payload.getCommand().getBusinessHours().stream().map(BusinessHoursDocument::new).collect(Collectors.toList());
        this.menuSections = new HashMap<>();
    }

    public void updateRestaurant(RestaurantUpdateCommand command) {
        if (command.getName() != null)
            this.name = command.getName();
        if (command.getDescription() != null)
            this.description = command.getDescription();
        if (command.getImageUrl() != null)
            this.imageUrl = command.getImageUrl();
        if (command.getCategoryName() != null)
            this.categoryName = command.getCategoryName();
        if (command.getPhone() != null)
            this.phone = command.getPhone();
        if (command.getAddress() != null)
            this.address = command.getAddress();
        if (command.getLatitude() != null && command.getLongitude() != null)
            this.location = new GeoJsonPoint(command.getLongitude(), command.getLatitude());
    }

    public void updateBusinessHours(BusinessHoursUpdateCommand command) {
        this.businessHours = command.getBusinessHours().stream().map(BusinessHoursDocument::new).collect(Collectors.toList());
    }

    public void createMenuSection(MenuSectionCreateEventPayload payload) {
        MenuSectionDocument menuSectionDocument = new MenuSectionDocument(payload);
        menuSections.put(payload.getMenuSectionId(), menuSectionDocument);
    }

    public void deleteMenuSection(MenuSectionDeleteEventPayload payload) {
        menuSections.remove(payload.getMenuSectionId());
    }

    public void updateMenuSection(MenuSectionUpdateEventPayload payload) {
        MenuSectionDocument menuSectionDocument = menuSections.get(payload.getMenuSectionId());
        menuSectionDocument.update(payload);
        menuSections.put(payload.getMenuSectionId(), menuSectionDocument);
    }

    public void createMenu(MenuCreateEventPayload payload) {
        MenuSectionDocument menuSectionDocument = menuSections.get(payload.getMenuSectionId());
        menuSectionDocument.createMenu(payload);
    }

    public void deleteMenu(MenuDeleteEventPayload payload) {
        MenuSectionDocument menuSectionDocument = menuSections.get(payload.getMenuSectionId());
        menuSectionDocument.deleteMenu(payload);
    }

    public void updateMenu(MenuUpdateEventPayload payload) {
        MenuSectionDocument menuSectionDocument = menuSections.get(payload.getMenuSectionId());
        menuSectionDocument.updateMenu(payload);
    }
}
