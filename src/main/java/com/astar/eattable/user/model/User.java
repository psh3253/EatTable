package com.astar.eattable.user.model;

import com.astar.eattable.common.model.BaseTimeEntity;
import com.astar.eattable.security.dto.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE user SET deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted = false")
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String nickname;

    @NotNull
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private final Role role = Role.ROLE_USER;

    @NotNull
    private final Boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public User(String email, String password, String nickname, String phoneNumber, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }
}
