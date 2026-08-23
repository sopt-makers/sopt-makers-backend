package org.sopt.makers.storage.db.playground.resolution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.resolution.ResolutionTag;
import org.sopt.makers.domain.playground.resolution.UserResolution;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.playground.resolution.converter.ResolutionTagListConverter;

@Entity
@Table(name = "user_resolution")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResolutionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer generation;

    @Convert(converter = ResolutionTagListConverter.class)
    private List<ResolutionTag> resolutionTags;

    @Builder
    private UserResolutionEntity(Long userId, String content, Integer generation, List<ResolutionTag> resolutionTags) {
        this.userId = userId;
        this.content = content;
        this.generation = generation;
        this.resolutionTags = resolutionTags;
    }

    public static UserResolutionEntity from(UserResolution resolution) {
        return UserResolutionEntity.builder()
                .userId(resolution.userId())
                .content(resolution.content())
                .generation(resolution.generation())
                .resolutionTags(resolution.resolutionTags())
                .build();
    }

    public UserResolution toDomain() {
        return new UserResolution(id, userId, content, generation, resolutionTags);
    }
}
