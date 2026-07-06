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
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.MeetingImageListConverter;
import org.sopt.makers.storage.db.crew.converter.MeetingJoinInfoConverter;
import org.sopt.makers.storage.db.crew.converter.MeetingJoinablePartListConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "meeting")
public class MeetingEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "meeting_demand_id")
  private Long meetingDemandId;

  @Column(nullable = false)
  private String title;

  @Column(name = "sub_title")
  private String subTitle;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MeetingCategory category;

  @Convert(converter = MeetingImageListConverter.class)
  @Column(name = "image_urls", nullable = false, columnDefinition = "TEXT")
  private List<MeetingImage> images;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;

  @Column(nullable = false)
  private Integer capacity;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "process_description", nullable = false, columnDefinition = "TEXT")
  private String processDescription;

  @Column(name = "activity_start_date")
  private LocalDateTime activityStartDate;

  @Column(name = "activity_end_date")
  private LocalDateTime activityEndDate;

  @Column(name = "leader_description", columnDefinition = "TEXT")
  private String leaderDescription;

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(name = "is_mentor_needed", nullable = false)
  private Boolean isMentorNeeded;

  @Column(name = "can_join_only_active_generation", nullable = false)
  private Boolean canJoinOnlyActiveGeneration;

  @Convert(converter = MeetingJoinInfoConverter.class)
  @Column(name = "join_info", columnDefinition = "TEXT")
  private MeetingJoinInfo joinInfo;

  @Column(name = "created_generation", nullable = false)
  private Integer createdGeneration;

  @Column(name = "target_active_generation")
  private Integer targetActiveGeneration;

  @Convert(converter = MeetingJoinablePartListConverter.class)
  @Column(name = "joinable_parts", columnDefinition = "TEXT")
  private List<MeetingJoinablePart> joinableParts;

  @Builder(access = PRIVATE)
  private MeetingEntity(
      Long id,
      Long userId,
      Long meetingDemandId,
      String title,
      String subTitle,
      MeetingCategory category,
      List<MeetingImage> images,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer capacity,
      String description,
      String processDescription,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      String leaderDescription,
      String note,
      Boolean isMentorNeeded,
      Boolean canJoinOnlyActiveGeneration,
      MeetingJoinInfo joinInfo,
      Integer createdGeneration,
      Integer targetActiveGeneration,
      List<MeetingJoinablePart> joinableParts) {
    this.id = id;
    this.userId = userId;
    this.meetingDemandId = meetingDemandId;
    this.title = title;
    this.subTitle = subTitle;
    this.category = category;
    this.images = images;
    this.startDate = startDate;
    this.endDate = endDate;
    this.capacity = capacity;
    this.description = description;
    this.processDescription = processDescription;
    this.activityStartDate = activityStartDate;
    this.activityEndDate = activityEndDate;
    this.leaderDescription = leaderDescription;
    this.note = note;
    this.isMentorNeeded = isMentorNeeded;
    this.canJoinOnlyActiveGeneration = canJoinOnlyActiveGeneration;
    this.joinInfo = joinInfo;
    this.createdGeneration = createdGeneration;
    this.targetActiveGeneration = targetActiveGeneration;
    this.joinableParts = joinableParts;
  }

  public Meeting toDomain() {
    return new Meeting(
        id,
        userId,
        meetingDemandId,
        title,
        subTitle,
        category,
        images,
        startDate,
        endDate,
        capacity,
        description,
        processDescription,
        activityStartDate,
        activityEndDate,
        leaderDescription,
        note,
        isMentorNeeded,
        canJoinOnlyActiveGeneration,
        joinInfo,
        createdGeneration,
        targetActiveGeneration,
        joinableParts,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static MeetingEntity fromDomain(Meeting meeting) {
    return MeetingEntity.builder()
        .id(meeting.id())
        .userId(meeting.userId())
        .meetingDemandId(meeting.meetingDemandId())
        .title(meeting.title())
        .subTitle(meeting.subTitle())
        .category(meeting.category())
        .images(meeting.images())
        .startDate(meeting.startDate())
        .endDate(meeting.endDate())
        .capacity(meeting.capacity())
        .description(meeting.description())
        .processDescription(meeting.processDescription())
        .activityStartDate(meeting.activityStartDate())
        .activityEndDate(meeting.activityEndDate())
        .leaderDescription(meeting.leaderDescription())
        .note(meeting.note())
        .isMentorNeeded(meeting.isMentorNeeded())
        .canJoinOnlyActiveGeneration(meeting.canJoinOnlyActiveGeneration())
        .joinInfo(meeting.joinInfo())
        .createdGeneration(meeting.createdGeneration())
        .targetActiveGeneration(meeting.targetActiveGeneration())
        .joinableParts(meeting.joinableParts())
        .build();
  }
}
