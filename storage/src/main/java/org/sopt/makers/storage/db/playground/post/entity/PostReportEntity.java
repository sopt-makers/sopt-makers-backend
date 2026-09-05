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
import org.sopt.makers.domain.playground.post.report.PostReport;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "report_post",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_report_post_post_reporter",
            columnNames = {"post_id", "reporter_id"}))
public class PostReportEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "reporter_id", nullable = false)
  private Long reporterId;

  @Builder(access = PRIVATE)
  private PostReportEntity(Long id, Long postId, Long reporterId) {
    this.id = id;
    this.postId = postId;
    this.reporterId = reporterId;
  }

  public PostReport toDomain() {
    return new PostReport(id, postId, reporterId, getCreatedAt(), getUpdatedAt());
  }

  public static PostReportEntity fromDomain(PostReport report) {
    return PostReportEntity.builder()
        .id(report.id())
        .postId(report.postId())
        .reporterId(report.reporterId())
        .build();
  }
}
