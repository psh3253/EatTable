package com.astar.eattable.restaurant.command;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class MenuSectionUpdateCommand {
    @NotNull
    @Size(min = 1, max = 50, message = "메뉴 섹션 이름은 1자 이상 50자 이하로 입력해주세요.")
    private String name;
}
