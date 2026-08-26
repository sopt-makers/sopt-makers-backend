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
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReportTarget;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "meeting_demand_report",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_meeting_demand_report_user_target",
            columnNames = {"user_id", "target_type", "target_id"}))
public class MeetingDemandReportEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false, length = 20)
  private MeetingDemandReportTarget targetType;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Builder(access = PRIVATE)
  private MeetingDemandReportEntity(
      Long id, Long userId, MeetingDemandReportTarget targetType, Long targetId) {
    this.id = id;
    this.userId = userId;
    this.targetType = targetType;
    this.targetId = targetId;
  }

  public MeetingDemandReport toDomain() {
    return new MeetingDemandReport(
        id, userId, targetType, targetId, getCreatedAt(), getUpdatedAt());
  }

  public static MeetingDemandReportEntity fromDomain(MeetingDemandReport report) {
    return MeetingDemandReportEntity.builder()
        .id(report.id())
        .userId(report.userId())
        .targetType(report.targetType())
        .targetId(report.targetId())
        .build();
  }
}
