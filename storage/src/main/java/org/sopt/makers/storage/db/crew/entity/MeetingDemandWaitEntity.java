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
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWait;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "meeting_demand_wait",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_meeting_demand_wait_demand_user",
            columnNames = {"meeting_demand_id", "user_id"}))
public class MeetingDemandWaitEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_demand_id", nullable = false)
  private Long meetingDemandId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder(access = PRIVATE)
  private MeetingDemandWaitEntity(Long id, Long meetingDemandId, Long userId) {
    this.id = id;
    this.meetingDemandId = meetingDemandId;
    this.userId = userId;
  }

  public MeetingDemandWait toDomain() {
    return new MeetingDemandWait(id, meetingDemandId, userId, getCreatedAt(), getUpdatedAt());
  }

  public static MeetingDemandWaitEntity fromDomain(MeetingDemandWait wait) {
    return MeetingDemandWaitEntity.builder()
        .id(wait.id())
        .meetingDemandId(wait.meetingDemandId())
        .userId(wait.userId())
        .build();
  }
}
