package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.payload.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuSectionDocument {
    private String name;
    private Map<Long, MenuDocument> menus;

    public MenuSectionDocument(MenuSectionCreateEventPayload payload) {
        this.name = payload.getCommand().getName();
        this.menus = new HashMap<>();
    }

    public void update(MenuSectionUpdateEventPayload payload) {
        this.name = payload.getCommand().getName();
    }

    public void createMenu(MenuCreateEventPayload payload) {
        this.menus.put(payload.getMenuId(), new MenuDocument(payload.getCommand()));
    }

    public void deleteMenu(MenuDeleteEventPayload payload) {
        this.menus.remove(payload.getMenuId());
    }

    public void updateMenu(MenuUpdateEventPayload payload) {
        this.menus.get(payload.getMenuId()).update(payload.getCommand());
    }
}
