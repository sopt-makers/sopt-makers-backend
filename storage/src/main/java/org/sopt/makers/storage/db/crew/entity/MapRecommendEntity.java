package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.MapRecommend;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "map_recommended",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_map_recommended_user_map",
            columnNames = {"user_id", "sopt_map_id"}))
public class MapRecommendEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "sopt_map_id", nullable = false)
  private Long soptMapId;

  @Column(nullable = false)
  private Boolean active;

  @Builder(access = PRIVATE)
  private MapRecommendEntity(Long id, Long userId, Long soptMapId, Boolean active) {
    this.id = id;
    this.userId = userId;
    this.soptMapId = soptMapId;
    this.active = active;
  }

  public MapRecommend toDomain() {
    return new MapRecommend(id, userId, soptMapId, active, getCreatedAt(), getUpdatedAt());
  }

  public static MapRecommendEntity fromDomain(MapRecommend recommend) {
    return MapRecommendEntity.builder()
        .id(recommend.id())
        .userId(recommend.userId())
        .soptMapId(recommend.soptMapId())
        .active(recommend.active())
        .build();
  }
}
