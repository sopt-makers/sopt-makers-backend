package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "recruit_part_introduction")
public class RecruitPartIntroductionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "generation_id", nullable = false)
  private Integer generationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "part", nullable = false, length = 20)
  private Part part;

  @Embedded private PartIntroductionEmbeddable introduction;

  public RecruitPartIntroduction toDomain() {
    return RecruitPartIntroduction.from(
        id, generationId, part, introduction.content, introduction.preference);
  }

  public static RecruitPartIntroductionEntity fromDomain(RecruitPartIntroduction intro) {
    RecruitPartIntroductionEntity entity = new RecruitPartIntroductionEntity();
    entity.id = intro.id();
    entity.generationId = intro.generationId();
    entity.part = intro.part();
    entity.introduction = PartIntroductionEmbeddable.of(intro.content(), intro.preferences());
    return entity;
  }
}
