package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.model.Menu;
import com.astar.eattable.restaurant.model.MenuSection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuSectionDocument {
    private Long id;
    private String name;
    List<MenuDocument> menus;

    public MenuSectionDocument(MenuSection menuSection, List<Menu> menuList) {
        this.id = menuSection.getId();
        this.name = menuSection.getName();
        this.menus = menuList.stream().map(MenuDocument::new).collect(Collectors.toList());
    }
}
