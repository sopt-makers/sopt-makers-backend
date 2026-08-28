package org.sopt.makers.storage.db.crew.entity;

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
import org.sopt.makers.domain.crew.notice.Notice;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "notice")
public class NoticeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(name = "sub_title", nullable = false)
  private String subTitle;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String contents;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Column(name = "expose_start_date", nullable = false)
  private LocalDateTime exposeStartDate;

  @Column(name = "expose_end_date", nullable = false)
  private LocalDateTime exposeEndDate;

  @Builder(access = PRIVATE)
  private NoticeEntity(
      Long id,
      String title,
      String subTitle,
      String contents,
      LocalDateTime createdDate,
      LocalDateTime exposeStartDate,
      LocalDateTime exposeEndDate) {
    this.id = id;
    this.title = title;
    this.subTitle = subTitle;
    this.contents = contents;
    this.createdDate = createdDate;
    this.exposeStartDate = exposeStartDate;
    this.exposeEndDate = exposeEndDate;
  }

  public Notice toDomain() {
    return new Notice(
        id,
        title,
        subTitle,
        contents,
        createdDate,
        exposeStartDate,
        exposeEndDate,
        getCreatedAt(),
        getUpdatedAt());
  }

  public static NoticeEntity fromDomain(Notice notice) {
    return NoticeEntity.builder()
        .id(notice.id())
        .title(notice.title())
        .subTitle(notice.subTitle())
        .contents(notice.contents())
        .createdDate(notice.createdDate())
        .exposeStartDate(notice.exposeStartDate())
        .exposeEndDate(notice.exposeEndDate())
        .build();
  }
}
