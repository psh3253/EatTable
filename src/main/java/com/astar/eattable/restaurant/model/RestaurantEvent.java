package com.astar.eattable.restaurant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
public class RestaurantEvent {
    @Id
    @UuidGenerator
    private UUID eventId;

    @NotNull
    private Long restaurantId;

    @NotNull
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @NotNull
    private boolean published = false;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    public void publish() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    @Builder
    public RestaurantEvent(Long restaurantId, String eventType, String payload) {
        this.restaurantId = restaurantId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    public static RestaurantEvent from(Long restaurantId, String eventType, String payload) {
        return RestaurantEvent.builder()
                .restaurantId(restaurantId)
                .eventType(eventType)
                .payload(payload)
                .build();
    }
}
