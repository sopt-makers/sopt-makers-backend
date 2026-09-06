package org.sopt.makers.storage.db.playground.post.entity;

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
import org.sopt.makers.domain.playground.post.report.PostCommentReport;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "report_comment",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_report_comment_comment_reporter",
            columnNames = {"comment_id", "reporter_id"}))
public class PostCommentReportEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "comment_id", nullable = false)
  private Long commentId;

  @Column(name = "reporter_id", nullable = false)
  private Long reporterId;

  @Builder(access = PRIVATE)
  private PostCommentReportEntity(Long id, Long commentId, Long reporterId) {
    this.id = id;
    this.commentId = commentId;
    this.reporterId = reporterId;
  }

  public PostCommentReport toDomain() {
    return new PostCommentReport(id, commentId, reporterId, getCreatedAt(), getUpdatedAt());
  }

  public static PostCommentReportEntity fromDomain(PostCommentReport report) {
    return PostCommentReportEntity.builder()
        .id(report.id())
        .commentId(report.commentId())
        .reporterId(report.reporterId())
        .build();
  }
}
