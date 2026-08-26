package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandOpenedNotification;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "meeting_demand_opened_notification")
public class MeetingDemandOpenedNotificationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_id", nullable = false, unique = true)
  private Long meetingId;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Builder(access = PRIVATE)
  private MeetingDemandOpenedNotificationEntity(Long id, Long meetingId, LocalDateTime sentAt) {
    this.id = id;
    this.meetingId = meetingId;
    this.sentAt = sentAt;
  }

  public MeetingDemandOpenedNotification toDomain() {
    return new MeetingDemandOpenedNotification(
        id, meetingId, sentAt, getCreatedAt(), getUpdatedAt());
  }

  public static MeetingDemandOpenedNotificationEntity fromDomain(
      MeetingDemandOpenedNotification notification) {
    return MeetingDemandOpenedNotificationEntity.builder()
        .id(notification.id())
        .meetingId(notification.meetingId())
        .sentAt(notification.sentAt())
        .build();
  }
}
