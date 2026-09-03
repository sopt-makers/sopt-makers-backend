package org.sopt.makers.storage.db.playground.post.entity;

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
import org.sopt.makers.domain.playground.post.mumu.MumuText;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "mumu_text")
public class MumuTextEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String text;

  @Column(nullable = false)
  private String category;

  @Column(name = "show_start_date", nullable = false)
  private LocalDateTime showStartDate;

  @Column(name = "show_end_date", nullable = false)
  private LocalDateTime showEndDate;

  @Builder(access = PRIVATE)
  private MumuTextEntity(
      Long id,
      String text,
      String category,
      LocalDateTime showStartDate,
      LocalDateTime showEndDate) {
    this.id = id;
    this.text = text;
    this.category = category;
    this.showStartDate = showStartDate;
    this.showEndDate = showEndDate;
  }

  public MumuText toDomain() {
    return new MumuText(
        id, text, category, showStartDate, showEndDate, getCreatedAt(), getUpdatedAt());
  }

  public static MumuTextEntity fromDomain(MumuText mumuText) {
    return MumuTextEntity.builder()
        .id(mumuText.id())
        .text(mumuText.text())
        .category(mumuText.category())
        .showStartDate(mumuText.showStartDate())
        .showEndDate(mumuText.showEndDate())
        .build();
  }
}
