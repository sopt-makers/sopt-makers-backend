package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.flash.Flash;
import org.sopt.makers.domain.crew.flash.FlashPlaceType;
import org.sopt.makers.domain.crew.flash.FlashTimingType;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.MeetingImageListConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "flash")
public class FlashEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "leader_user_id", nullable = false)
  private Long leaderUserId;

  @Column(name = "meeting_id", nullable = false, unique = true)
  private Long meetingId;

  @Column(nullable = false, length = 30)
  private String title;

  @Column(name = "description", nullable = false, length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "timing_type", nullable = false, length = 30)
  private FlashTimingType timingType;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;

  @Column(name = "activity_start_date", nullable = false)
  private LocalDateTime activityStartDate;

  @Column(name = "activity_end_date", nullable = false)
  private LocalDateTime activityEndDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "place_type", nullable = false, length = 30)
  private FlashPlaceType placeType;

  @Column(name = "place")
  private String place;

  @Column(name = "minimum_capacity", nullable = false)
  private Integer minimumCapacity;

  @Column(name = "maximum_capacity", nullable = false)
  private Integer maximumCapacity;

  @Column(name = "created_generation", nullable = false)
  private Integer createdGeneration;

  @Convert(converter = MeetingImageListConverter.class)
  @Column(name = "image_urls", nullable = false, columnDefinition = "TEXT")
  private List<MeetingImage> images;

  @Builder(access = PRIVATE)
  private FlashEntity(
      Long id,
      Long leaderUserId,
      Long meetingId,
      String title,
      String description,
      FlashTimingType timingType,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      FlashPlaceType placeType,
      String place,
      Integer minimumCapacity,
      Integer maximumCapacity,
      Integer createdGeneration,
      List<MeetingImage> images) {
    this.id = id;
    this.leaderUserId = leaderUserId;
    this.meetingId = meetingId;
    this.title = title;
    this.description = description;
    this.timingType = timingType;
    this.startDate = startDate;
    this.endDate = endDate;
    this.activityStartDate = activityStartDate;
    this.activityEndDate = activityEndDate;
    this.placeType = placeType;
    this.place = place;
    this.minimumCapacity = minimumCapacity;
    this.maximumCapacity = maximumCapacity;
    this.createdGeneration = createdGeneration;
    this.images = images;
  }

  public Flash toDomain() {
    return new Flash(
        id,
        leaderUserId,
        meetingId,
        title,
        description,
        timingType,
        startDate,
        endDate,
        activityStartDate,
        activityEndDate,
        placeType,
        place,
        minimumCapacity,
        maximumCapacity,
        createdGeneration,
        images,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static FlashEntity fromDomain(Flash flash) {
    return FlashEntity.builder()
        .id(flash.id())
        .leaderUserId(flash.leaderUserId())
        .meetingId(flash.meetingId())
        .title(flash.title())
        .description(flash.description())
        .timingType(flash.timingType())
        .startDate(flash.startDate())
        .endDate(flash.endDate())
        .activityStartDate(flash.activityStartDate())
        .activityEndDate(flash.activityEndDate())
        .placeType(flash.placeType())
        .place(flash.place())
        .minimumCapacity(flash.minimumCapacity())
        .maximumCapacity(flash.maximumCapacity())
        .createdGeneration(flash.createdGeneration())
        .images(flash.images())
        .build();
  }
}
