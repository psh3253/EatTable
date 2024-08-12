package com.astar.eattable.restaurant.command;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class MenuUpdateCommand {
    @Size(min = 1, max = 50, message = "메뉴 이름은 1자 이상 50자 이하로 입력해주세요.")
    private String name;

    @Min(value = 0, message = "가격은 0원 이상으로 입력해주세요.")
    private Integer price;

    @Size(min = 1, max = 500, message = "메뉴 설명은 1자 이상 500자 이하로 입력해주세요.")
    private String description;

    private String imageUrl;
}
