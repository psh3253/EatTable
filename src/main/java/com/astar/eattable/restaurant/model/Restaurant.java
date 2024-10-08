package com.astar.eattable.restaurant.model;

import com.astar.eattable.common.model.BaseTimeEntity;
import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE restaurant SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "restaurant")
@Entity
public class Restaurant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    private String imageUrl;

    @NotNull
    private String categoryName;

    @NotNull
    private String phone;

    @NotNull
    private String address;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Integer reservationDuration = 60;

    @NotNull
    private Boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private final Boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public Restaurant(String name, String description, String imageUrl, String categoryName, String phone, String address, Double latitude, Double longitude, User user) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    public void update(RestaurantUpdateCommand command) {
        if (command.getName() != null) {
            this.name = command.getName();
        }
        if (command.getDescription() != null) {
            this.description = command.getDescription();
        }
        if (command.getImageUrl() != null) {
            this.imageUrl = command.getImageUrl();
        }
        if (command.getCategoryName() != null) {
            this.categoryName = command.getCategoryName();
        }
        if (command.getPhone() != null) {
            this.phone = command.getPhone();
        }
        if (command.getAddress() != null) {
            this.address = command.getAddress();
        }
        if (command.getLatitude() != null) {
            this.latitude = command.getLatitude();
        }
        if (command.getLongitude() != null) {
            this.longitude = command.getLongitude();
        }
    }

    public void updateReservationDuration(Integer duration) {
        this.reservationDuration = duration;
    }

    public void open() {
        this.isAvailable = true;
    }

    public void close() {
        this.isAvailable = false;
    }

    public void setIdForTest(Long id) {
        this.id = id;
    }
}
