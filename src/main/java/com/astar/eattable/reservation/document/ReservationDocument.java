package com.astar.eattable.reservation.document;

import com.astar.eattable.reservation.payload.ReservationCreatePayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "reservation")
public class ReservationDocument {
    @Id
    private Long id;

    private Long restaurantId;

    private String restaurantName;

    private String date;

    private String time;

    private Long userId;

    private String userNickname;

    private Integer capacity;

    private String request;

    public ReservationDocument(ReservationCreatePayload payload) {
        this.id = payload.getReservationId();
        this.restaurantId = payload.getCommand().getRestaurantId();
        this.restaurantName = payload.getRestaurantName();
        this.date = payload.getCommand().getDate();
        this.time = payload.getCommand().getTime();
        this.userId = payload.getUserId();
        this.userNickname = payload.getUserNickname();
        this.capacity = payload.getCommand().getCapacity();
        this.request = payload.getCommand().getRequest();
    }
}
