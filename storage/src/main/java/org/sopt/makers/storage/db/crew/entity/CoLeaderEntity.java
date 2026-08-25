package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.CoLeader;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "co_leader")
public class CoLeaderEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_id", nullable = false)
  private Long meetingId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder(access = PRIVATE)
  private CoLeaderEntity(Long id, Long meetingId, Long userId) {
    this.id = id;
    this.meetingId = meetingId;
    this.userId = userId;
  }

  public CoLeader toDomain() {
    return new CoLeader(id, meetingId, userId, getCreatedAt(), getUpdatedAt());
  }

  public static CoLeaderEntity fromDomain(CoLeader coLeader) {
    return CoLeaderEntity.builder()
        .id(coLeader.id())
        .meetingId(coLeader.meetingId())
        .userId(coLeader.userId())
        .build();
  }
}
