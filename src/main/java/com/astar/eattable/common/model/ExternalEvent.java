package com.astar.eattable.common.model;

import com.astar.eattable.user.model.User;
import jakarta.persistence.*;
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
public class ExternalEvent {
    @Id
    @UuidGenerator
    private UUID eventId;

    @NotNull
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @NotNull
    private final boolean published = false;

    @NotNull
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime publishedAt;

    @Builder
    public ExternalEvent(String eventType, String payload, User user) {
        this.eventType = eventType;
        this.payload = payload;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public static ExternalEvent from(String eventType, String payload, User user) {
        return ExternalEvent.builder()
                .eventType(eventType)
                .payload(payload)
                .user(user)
                .build();
    }
}
