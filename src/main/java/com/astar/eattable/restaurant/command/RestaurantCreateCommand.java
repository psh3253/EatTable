package com.astar.eattable.restaurant.command;

import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.user.model.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateCommand {
    @NotNull
    @Size(min = 2, max = 50, message = "식당 이름은 2자 이상 50자 이하로 입력해주세요.")
    private String name;

    @NotNull
    @Size(min = 2, max = 100, message = "식당 주소는 2자 이상 100자 이하로 입력해주세요.")
    private String address;

    @NotNull
    @Size(min = 10, max = 15, message = "전화번호는 10자 이상 15자 이하로 입력해주세요.")
    private String phone;

    @NotNull
    private String imageUrl;

    @NotNull
    @Size(min = 2, max = 30, message = "카테고리 이름은 2자 이상 30자 이하로 입력해주세요.")
    private String categoryName;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    public Restaurant toEntity(User createdBy) {
        return Restaurant.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .imageUrl(imageUrl)
                .categoryName(categoryName)
                .latitude(latitude)
                .longitude(longitude)
                .createdBy(createdBy)
                .build();
    }
}
