package org.sopt.makers.storage.db.crew.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.sopt.makers.domain.crew.dining.model.*;
import org.sopt.makers.domain.crew.dining.model.enums.*;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dining_round")
public class DiningRoundEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = true)
  private Long id;

  @Column(name = "meeting_id", nullable = false)
  private Long meetingId;

  @Column(name = "place", nullable = true, length = 30)
  private String place;

  @Column(name = "naver_map_link", nullable = true, length = 500)
  private String naverMapLink;

  @Column(name = "kakao_map_link", nullable = true, length = 500)
  private String kakaoMapLink;

  @Column(name = "start_time", nullable = true)
  private LocalTime startTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private DiningRoundStatus status;

  @Column(name = "closed_at", nullable = true)
  private LocalDateTime closedAt;

  @Column(name = "is_place_modified", nullable = false)
  private boolean isPlaceModified;

  @Column(name = "is_link_modified", nullable = false)
  private boolean isLinkModified;

  @Column(name = "is_start_time_modified", nullable = false)
  private boolean isStartTimeModified;

  public DiningRound toDomain() {
    return new DiningRound(
        id,
        meetingId,
        place,
        naverMapLink,
        kakaoMapLink,
        startTime,
        status,
        closedAt,
        isPlaceModified,
        isLinkModified,
        isStartTimeModified,
        getCreatedAt());
  }

  public static DiningRoundEntity fromDomain(DiningRound value) {
    var entity = new DiningRoundEntity();
    entity.id = value.getId();
    entity.meetingId = value.getMeetingId();
    entity.place = value.getPlace();
    entity.naverMapLink = value.getNaverMapLink();
    entity.kakaoMapLink = value.getKakaoMapLink();
    entity.startTime = value.getStartTime();
    entity.status = value.getStatus();
    entity.closedAt = value.getClosedAt();
    entity.isPlaceModified = value.isPlaceModified();
    entity.isLinkModified = value.isLinkModified();
    entity.isStartTimeModified = value.isStartTimeModified();
    return entity;
  }

  public void updateFromDomain(DiningRound value) {
    this.meetingId = value.getMeetingId();
    this.place = value.getPlace();
    this.naverMapLink = value.getNaverMapLink();
    this.kakaoMapLink = value.getKakaoMapLink();
    this.startTime = value.getStartTime();
    this.status = value.getStatus();
    this.closedAt = value.getClosedAt();
    this.isPlaceModified = value.isPlaceModified();
    this.isLinkModified = value.isLinkModified();
    this.isStartTimeModified = value.isStartTimeModified();
  }
}
