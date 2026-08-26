package org.sopt.makers.storage.db.app.soptletter.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "sopt_letter_topic")
public class SoptLetterTopicEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "cta_text", length = 50)
  private String ctaText;

  @Column(name = "is_default", nullable = false)
  private boolean isDefault;

  @Column(name = "started_at", nullable = false)
  private LocalDateTime startedAt;

  @Column(name = "ended_at", nullable = false)
  private LocalDateTime endedAt;

  public SoptLetterTopic toDomain() {
    return new SoptLetterTopic(id, title, ctaText, isDefault, startedAt, endedAt, getCreatedAt());
  }
}
