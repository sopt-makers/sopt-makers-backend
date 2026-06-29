package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "part_type")
public class RecruitPartEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "generation_id", nullable = false)
  private Integer generationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "part_type", nullable = false, length = 20)
  private Part partType;

  @Column(name = "description", nullable = false, length = 1000)
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "curriculums", nullable = false, columnDefinition = "text")
  private java.util.List<String> curriculums;

  public RecruitPartInfo toDomain() {
    return new RecruitPartInfo(id, generationId, partType, description, curriculums);
  }
}
