package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.LongListConverter;
import org.sopt.makers.storage.db.crew.converter.MapTagListConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "sopt_map")
public class SoptMapEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Convert(converter = LongListConverter.class)
  @Column(name = "nearby_station_ids", nullable = false, columnDefinition = "TEXT")
  private List<Long> nearbyStationIds;

  @Column(name = "place_name", nullable = false, unique = true)
  private String placeName;

  @Column(nullable = false, length = 500)
  private String description;

  @Convert(converter = MapTagListConverter.class)
  @Column(name = "map_tags", nullable = false, columnDefinition = "TEXT")
  private List<MapTag> mapTags;

  @Column(name = "naver_link")
  private String naverLink;

  @Column(name = "kakao_link")
  private String kakaoLink;

  @Column(name = "creator_id", nullable = false)
  private Long creatorId;

  @Builder(access = PRIVATE)
  private SoptMapEntity(
      Long id,
      List<Long> nearbyStationIds,
      String placeName,
      String description,
      List<MapTag> mapTags,
      String naverLink,
      String kakaoLink,
      Long creatorId) {
    this.id = id;
    this.nearbyStationIds = nearbyStationIds;
    this.placeName = placeName;
    this.description = description;
    this.mapTags = mapTags;
    this.naverLink = naverLink;
    this.kakaoLink = kakaoLink;
    this.creatorId = creatorId;
  }

  public SoptMap toDomain() {
    return new SoptMap(
        id,
        nearbyStationIds,
        placeName,
        description,
        mapTags,
        naverLink,
        kakaoLink,
        creatorId,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static SoptMapEntity fromDomain(SoptMap map) {
    return SoptMapEntity.builder()
        .id(map.id())
        .nearbyStationIds(map.nearbyStationIds())
        .placeName(map.placeName())
        .description(map.description())
        .mapTags(map.mapTags())
        .naverLink(map.naverLink())
        .kakaoLink(map.kakaoLink())
        .creatorId(map.creatorId())
        .build();
  }
}
