package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.payload.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "menu_section_map")
public class MenuSectionMapDocument {
    @Id
    private Long restaurantId;

    private Map<Long, MenuSectionDocument> menuSections;

    public MenuSectionMapDocument(Long restaurantId) {
        this.restaurantId = restaurantId;
        menuSections = new HashMap<>();
    }

    public void createMenuSection(MenuSectionCreateEventPayload payload) {
        menuSections.put(payload.getMenuSectionId(), new MenuSectionDocument(payload));
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
