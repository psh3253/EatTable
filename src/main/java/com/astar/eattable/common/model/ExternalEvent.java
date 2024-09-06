package com.astar.eattable.common.model;

import com.astar.eattable.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

    @NotNull
    private Long keyValue;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @NotNull
    private final boolean published = false;

    @ElementCollection
    @MapKeyColumn(name = "domain")
    @Column(name = "consumed")
    private Map<String, Boolean> domainConsumed = new HashMap<>();

    @NotNull
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime publishedAt;

    @Builder
    public ExternalEvent(String eventType, Long keyValue, String payload, User user) {
        this.eventType = eventType;
        this.keyValue = keyValue;
        this.payload = payload;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public static ExternalEvent from(String eventType, Long keyValue, String payload, User user) {
        return ExternalEvent.builder()
                .eventType(eventType)
                .keyValue(keyValue)
                .payload(payload)
                .user(user)
                .build();
    }

    public void consumeEvent(String domain) {
        domainConsumed.put(domain, true);
    }

    public boolean isConsumed(String domain) {
        return domainConsumed.getOrDefault(domain, false);
    }
}
