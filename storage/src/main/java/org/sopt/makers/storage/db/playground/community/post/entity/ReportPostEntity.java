package org.sopt.makers.storage.db.playground.community.post.entity;

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
import org.sopt.makers.domain.playground.community.post.ReportPost;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "report_post")
// 신고 이력 테이블로 BaseEntity(감사 컬럼)를 상속하지 않고 생성 시각만 보관함
public class ReportPostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "reporter_id", nullable = false)
  private Long reporterId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Builder(access = PROTECTED)
  private ReportPostEntity(Long postId, Long reporterId, LocalDateTime createdAt) {
    this.postId = postId;
    this.reporterId = reporterId;
    this.createdAt = createdAt;
  }

  public static ReportPostEntity from(ReportPost reportPost) {
    return ReportPostEntity.builder()
        .postId(reportPost.postId())
        .reporterId(reportPost.reporterId())
        .createdAt(LocalDateTime.now())
        .build();
  }

  public ReportPost toDomain() {
    return new ReportPost(id, postId, reporterId, createdAt);
  }
}
