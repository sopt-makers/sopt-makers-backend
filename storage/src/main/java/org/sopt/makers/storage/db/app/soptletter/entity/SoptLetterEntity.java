package org.sopt.makers.storage.db.app.soptletter.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterColor;
import org.sopt.makers.domain.app.soptletter.SoptLetterShapeType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "sopt_letter",
    indexes = {
      @Index(name = "idx_sopt_letter_topic_id_id", columnList = "topic_id, id"),
      @Index(
          name = "idx_sopt_letter_author_profile_id_created_at",
          columnList = "author_profile_id, created_at")
    })
public class SoptLetterEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "author_profile_id", nullable = false)
  private Long authorProfileId;

  @Column(name = "topic_id", nullable = false)
  private Long topicId;

  @Column(name = "degree", nullable = false)
  private Double degree;

  @Column(name = "message", nullable = false, columnDefinition = "TEXT")
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(name = "color", length = 20)
  private SoptLetterColor color;

  @Enumerated(EnumType.STRING)
  @Column(name = "shape_type", length = 20)
  private SoptLetterShapeType shapeType;

  @Column(name = "like_count", nullable = false)
  private int likeCount;

  private SoptLetterEntity(SoptLetter soptLetter) {
    this.authorProfileId = soptLetter.authorProfileId();
    this.topicId = soptLetter.topicId();
    this.degree = soptLetter.degree();
    this.message = soptLetter.message();
    this.color = soptLetter.color();
    this.shapeType = soptLetter.shapeType();
    this.likeCount = soptLetter.likeCount();
  }

  public static SoptLetterEntity from(SoptLetter soptLetter) {
    return new SoptLetterEntity(soptLetter);
  }

  public SoptLetter toDomain() {
    return new SoptLetter(
        id,
        authorProfileId,
        topicId,
        degree,
        message,
        color,
        shapeType,
        likeCount,
        getCreatedAt(),
        getUpdatedAt());
  }
}
