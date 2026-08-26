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
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWaitHistory;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "meeting_demand_wait_history",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_meeting_demand_wait_history_demand_user",
            columnNames = {"meeting_demand_id", "user_id"}))
public class MeetingDemandWaitHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_demand_id", nullable = false)
  private Long meetingDemandId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder(access = PRIVATE)
  private MeetingDemandWaitHistoryEntity(Long id, Long meetingDemandId, Long userId) {
    this.id = id;
    this.meetingDemandId = meetingDemandId;
    this.userId = userId;
  }

  public MeetingDemandWaitHistory toDomain() {
    return new MeetingDemandWaitHistory(
        id, meetingDemandId, userId, getCreatedAt(), getUpdatedAt());
  }

  public static MeetingDemandWaitHistoryEntity fromDomain(MeetingDemandWaitHistory history) {
    return MeetingDemandWaitHistoryEntity.builder()
        .id(history.id())
        .meetingDemandId(history.meetingDemandId())
        .userId(history.userId())
        .build();
  }
}
