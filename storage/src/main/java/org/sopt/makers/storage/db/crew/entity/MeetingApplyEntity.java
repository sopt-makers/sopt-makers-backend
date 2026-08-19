package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.MeetingApplyType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "apply")
public class MeetingApplyEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MeetingApplyType type;

  @Column(name = "meeting_id", nullable = false)
  private Long meetingId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "applied_date", nullable = false)
  private LocalDateTime appliedDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MeetingApplyStatus status;

  @Builder(access = PRIVATE)
  private MeetingApplyEntity(
      Long id,
      MeetingApplyType type,
      Long meetingId,
      Long userId,
      String content,
      LocalDateTime appliedDate,
      MeetingApplyStatus status) {
    this.id = id;
    this.type = type;
    this.meetingId = meetingId;
    this.userId = userId;
    this.content = content;
    this.appliedDate = appliedDate;
    this.status = status;
  }

  public MeetingApply toDomain() {
    return new MeetingApply(
        id, type, meetingId, userId, content, appliedDate, status, getCreatedAt(), getUpdatedAt());
  }

  public static MeetingApplyEntity fromDomain(MeetingApply apply) {
    return MeetingApplyEntity.builder()
        .id(apply.id())
        .type(apply.type())
        .meetingId(apply.meetingId())
        .userId(apply.userId())
        .content(apply.content())
        .appliedDate(apply.appliedDate())
        .status(apply.status())
        .build();
  }
}
