package com.astar.eattable.restaurant.dto;

import com.astar.eattable.restaurant.document.MenuSectionDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MenuSectionDTO {
    private final Long id;
    private final String name;
    private final List<MenuDTO> menus;

    public MenuSectionDTO(MenuSectionDocument menuSectionDocument) {
        this.id = menuSectionDocument.getId();
        this.name = menuSectionDocument.getName();
        this.menus = menuSectionDocument.getMenus().stream().map(MenuDTO::new).collect(Collectors.toList());
    }
}
