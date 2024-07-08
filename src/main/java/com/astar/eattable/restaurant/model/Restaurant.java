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
@Entity
public class Restaurant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    @NotNull
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public Restaurant(Long id, String name, String imageUrl, String categoryName, String phone, String address, Double latitude, Double longitude, User createdBy) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
    }

    public void update(RestaurantUpdateCommand command) {
        if (command.getName() != null) {
            this.name = command.getName();
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
}
