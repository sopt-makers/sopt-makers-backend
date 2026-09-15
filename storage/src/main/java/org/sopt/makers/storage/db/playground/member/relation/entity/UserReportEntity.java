package org.sopt.makers.storage.db.playground.member.relation.entity;

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
import org.sopt.makers.domain.playground.member.relation.UserReport;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member_report")
public class UserReportEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "reporter_id", nullable = false)
  private Long reporterUserId;

  @Column(name = "reported_member_id", nullable = false)
  private Long reportedUserId;

  @Column(name = "reason", columnDefinition = "TEXT")
  private String reason;

  @Builder(access = PRIVATE)
  private UserReportEntity(Long id, Long reporterUserId, Long reportedUserId, String reason) {
    this.id = id;
    this.reporterUserId = reporterUserId;
    this.reportedUserId = reportedUserId;
    this.reason = reason;
  }

  public UserReport toDomain() {
    return new UserReport(
        id, reporterUserId, reportedUserId, reason, getCreatedAt(), getUpdatedAt());
  }

  public static UserReportEntity fromDomain(UserReport userReport) {
    return UserReportEntity.builder()
        .id(userReport.id())
        .reporterUserId(userReport.reporterUserId())
        .reportedUserId(userReport.reportedUserId())
        .reason(userReport.reason())
        .build();
  }
}
