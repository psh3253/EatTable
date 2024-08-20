package com.astar.eattable.reservation.command;

import com.astar.eattable.reservation.model.Reservation;
import com.astar.eattable.reservation.model.TableAvailability;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.user.model.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class ReservationCreateCommand {

    @NotNull
    private Long restaurantId;

    @NotNull
    @Size(min = 10, max = 10, message = "날짜는 yyyy-MM-dd 형식으로 입력해주세요.")
    private String date;

    @NotNull
    @Size(min = 5, max = 5, message = "시간은 HH:mm 형식으로 입력해주세요.")
    private String time;

    @NotNull
    @Min(value = 1, message = "예약 인원은 1명 이상으로 입력해주세요.")
    @Max(value = 4, message = "예약 인원은 4명 이하로 입력해주세요.")
    private Integer capacity;

    @Size(min = 1, max = 100, message = "요청사항은 1자 이상 100자 이하로 입력해주세요.")
    private String request;

    public Reservation toEntity(Restaurant restaurant, TableAvailability tableAvailability, User user) {
        return Reservation.builder()
                .restaurant(restaurant)
                .tableAvailability(tableAvailability)
                .capacity(capacity)
                .request(request)
                .user(user)
                .build();
    }
}
