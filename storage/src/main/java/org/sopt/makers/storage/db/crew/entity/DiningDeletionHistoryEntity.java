package org.sopt.makers.storage.db.crew.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.domain.crew.dining.model.*;
import org.sopt.makers.domain.crew.dining.model.enums.*;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dining_deletion_history")
public class DiningDeletionHistoryEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = true)
  private Long id;

  @Column(name = "meeting_id", nullable = false)
  private Long meetingId;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false)
  private DiningDeletionTargetType targetType;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Column(name = "deleted_by_user_id", nullable = false)
  private Long deletedByUserId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "snapshot", nullable = false)
  private Map<String, Object> snapshot;

  public DiningDeletionHistory toDomain() {
    return new DiningDeletionHistory(
        id, meetingId, targetType, targetId, deletedByUserId, snapshot, getCreatedAt());
  }

  public static DiningDeletionHistoryEntity fromDomain(DiningDeletionHistory value) {
    var entity = new DiningDeletionHistoryEntity();
    entity.id = value.getId();
    entity.meetingId = value.getMeetingId();
    entity.targetType = value.getTargetType();
    entity.targetId = value.getTargetId();
    entity.deletedByUserId = value.getDeletedByUserId();
    entity.snapshot = value.getSnapshot();
    return entity;
  }
}
