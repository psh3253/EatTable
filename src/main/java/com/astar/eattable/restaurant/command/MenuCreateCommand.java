package com.astar.eattable.restaurant.command;

import com.astar.eattable.restaurant.model.Menu;
import com.astar.eattable.restaurant.model.MenuSection;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreateCommand {
    @NotNull
    @Size(min = 1, max = 50, message = "메뉴 이름은 1자 이상 50자 이하로 입력해주세요.")
    private String name;

    @Size(min = 1, max = 500, message = "메뉴 설명은 1자 이상 500자 이하로 입력해주세요.")
    private String description;

    @NotNull
    @Min(value = 0, message = "가격은 0원 이상으로 입력해주세요.")
    private Integer price;

    private String imageUrl;

    public Menu toEntity(MenuSection menuSection) {
        return Menu.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .restaurant(menuSection.getRestaurant())
                .menuSection(menuSection)
                .build();
    }
}
