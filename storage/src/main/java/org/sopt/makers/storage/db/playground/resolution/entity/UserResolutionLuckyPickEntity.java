package org.sopt.makers.storage.db.playground.resolution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.resolution.UserResolutionLuckyPick;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Table(name = "user_resolution_lucky_pick")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResolutionLuckyPickEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private boolean result = false;

    @Column(nullable = false)
    private boolean hasDrawn = false;

    private UserResolutionLuckyPickEntity(Long userId) {
        this.userId = userId;
    }

    public static UserResolutionLuckyPickEntity from(UserResolutionLuckyPick luckyPick) {
        return new UserResolutionLuckyPickEntity(luckyPick.userId());
    }

    public void update(UserResolutionLuckyPick luckyPick) {
        this.result = luckyPick.isResult();
        this.hasDrawn = luckyPick.isHasDrawn();
    }

    public UserResolutionLuckyPick toDomain() {
        return new UserResolutionLuckyPick(id, userId, result, hasDrawn);
    }
}
