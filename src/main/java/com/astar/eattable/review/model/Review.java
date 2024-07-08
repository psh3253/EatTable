package com.astar.eattable.review.model;

import com.astar.eattable.common.model.BaseTimeEntity;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@SQLRestriction("deleted = false")
@Entity
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @NotNull
    private String content;

    @NotNull
    private Double score;

    @NotNull
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private Boolean deleted;

    private LocalDateTime deletedAt;

    public Review(Restaurant restaurant, String content, Double score, String imageUrl, User user) {
        this.restaurant = restaurant;
        this.content = content;
        this.score = score;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
