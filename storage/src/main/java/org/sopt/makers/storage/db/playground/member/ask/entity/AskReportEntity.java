package org.sopt.makers.storage.db.playground.member.ask.entity;

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
import org.hibernate.annotations.CreationTimestamp;
import org.sopt.makers.domain.playground.member.ask.AskReport;

// question_report 테이블에는 updated_at 컬럼이 없어 BaseEntity를 상속하지 않는다.
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "question_report")
public class AskReportEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long id;

  @Column(name = "question_id", nullable = false)
  private Long questionId;

  @Column(name = "reporter_id", nullable = false)
  private Long reporterUserId;

  @Column(name = "reason")
  private String reason;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder(access = PRIVATE)
  private AskReportEntity(Long id, Long questionId, Long reporterUserId, String reason) {
    this.id = id;
    this.questionId = questionId;
    this.reporterUserId = reporterUserId;
    this.reason = reason;
  }

  public AskReport toDomain() {
    return new AskReport(id, questionId, reporterUserId, reason, createdAt);
  }

  public static AskReportEntity fromDomain(AskReport askReport) {
    return AskReportEntity.builder()
        .id(askReport.id())
        .questionId(askReport.questionId())
        .reporterUserId(askReport.reporterUserId())
        .reason(askReport.reason())
        .build();
  }
}
