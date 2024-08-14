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

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateCommand {
    @NotNull
    @Size(min = 1, max = 50, message = "식당 이름은 1자 이상 50자 이하로 입력해주세요.")
    private String name;

    @NotNull
    @Size(min = 2, max = 500, message = "식당 설명은 2자 이상 500자 이하로 입력해주세요.")
    private String description;

    @NotNull
    @Size(min = 2, max = 100, message = "식당 주소는 2자 이상 100자 이하로 입력해주세요.")
    private String address;

    @NotNull
    @Size(min = 10, max = 15, message = "전화번호는 10자 이상 15자 이하로 입력해주세요.")
    private String phone;

    @NotNull
    private String imageUrl;

    @NotNull
    @Size(min = 1, max = 30, message = "카테고리 이름은 1자 이상 30자 이하로 입력해주세요.")
    private String categoryName;

    @NotNull
    private List<BusinessHoursCommand> businessHours;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    public Restaurant toEntity(User user) {
        return Restaurant.builder()
                .name(name)
                .address(address)
                .description(description)
                .phone(phone)
                .imageUrl(imageUrl)
                .categoryName(categoryName)
                .latitude(latitude)
                .longitude(longitude)
                .user(user)
                .build();
    }
}
