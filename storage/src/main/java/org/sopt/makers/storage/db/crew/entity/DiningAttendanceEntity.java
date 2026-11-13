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
@Table(
    name = "dining_attendance",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_dining_attendance_round_user",
            columnNames = {"round_id", "user_id"}))
public class DiningAttendanceEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = true)
  private Long id;

  @Column(name = "round_id", nullable = false)
  private Long roundId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "payer_status", nullable = true)
  private DiningPayerStatus payerStatus;

  @Column(name = "payer_requested_at", nullable = true)
  private LocalDateTime payerRequestedAt;

  public DiningAttendance toDomain() {
    return new DiningAttendance(id, roundId, userId, payerStatus, payerRequestedAt, getCreatedAt());
  }

  public static DiningAttendanceEntity fromDomain(DiningAttendance value) {
    var entity = new DiningAttendanceEntity();
    entity.id = value.getId();
    entity.roundId = value.getRoundId();
    entity.userId = value.getUserId();
    entity.payerStatus = value.getPayerStatus();
    entity.payerRequestedAt = value.getPayerRequestedAt();
    return entity;
  }

  public void updateFromDomain(DiningAttendance value) {
    this.roundId = value.getRoundId();
    this.userId = value.getUserId();
    this.payerStatus = value.getPayerStatus();
    this.payerRequestedAt = value.getPayerRequestedAt();
  }
}
