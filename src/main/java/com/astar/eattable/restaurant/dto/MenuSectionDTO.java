package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.MenuSectionDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MenuSectionDTO {
    private final String name;
    private final Map<Long, MenuDTO> menus;

    public MenuSectionDTO(MenuSectionDocument menuSectionDocument) {
        this.name = menuSectionDocument.getName();
        this.menus = menuSectionDocument.getMenus().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new MenuDTO(entry.getValue())));
    }
}
