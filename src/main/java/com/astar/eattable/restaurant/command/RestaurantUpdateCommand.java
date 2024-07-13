package com.astar.eattable.restaurant.command;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantUpdateCommand {
    @Size(min = 2, max = 50, message = "식당 이름은 2자 이상 50자 이하로 입력해주세요.")
    private String name;

    @Size(min = 2, max = 500, message = "식당 설명은 2자 이상 500자 이하로 입력해주세요.")
    private String description;

    @Size(min = 2, max = 100, message = "식당 주소는 2자 이상 100자 이하로 입력해주세요.")
    private String address;

    @Size(min = 10, max = 15, message = "전화번호는 10자 이상 15자 이하로 입력해주세요.")
    private String phone;

    private String imageUrl;

    @Size(min = 2, max = 30, message = "카테고리 이름은 2자 이상 30자 이하로 입력해주세요.")
    private String categoryName;

    private Double latitude;
    private Double longitude;
}
