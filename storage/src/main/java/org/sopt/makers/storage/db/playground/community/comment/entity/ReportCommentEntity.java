package org.sopt.makers.storage.db.playground.community.comment.entity;

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
import org.sopt.makers.domain.playground.community.comment.ReportComment;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "report_comment")
// 신고 이력 테이블로 BaseEntity(감사 컬럼)를 상속하지 않고 생성 시각만 보관함
public class ReportCommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "comment_id", nullable = false)
  private Long commentId;

  @Column(name = "reporter_id", nullable = false)
  private Long reporterId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Builder(access = PROTECTED)
  private ReportCommentEntity(Long commentId, Long reporterId, LocalDateTime createdAt) {
    this.commentId = commentId;
    this.reporterId = reporterId;
    this.createdAt = createdAt;
  }

  public static ReportCommentEntity from(ReportComment reportComment) {
    return ReportCommentEntity.builder()
        .commentId(reportComment.commentId())
        .reporterId(reportComment.reporterId())
        .createdAt(LocalDateTime.now())
        .build();
  }

  public ReportComment toDomain() {
    return new ReportComment(id, commentId, reporterId, createdAt);
  }
}
