package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "lectures")
public class LectureEntity extends BaseEntity {

  @Id
  @Column(name = "lecture_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
  private Long id;

  private String name;

  @Enumerated(EnumType.STRING)
  private Part part;

  private int generation;

  private String place;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  private LectureAttribute attribute;

  @Enumerated(EnumType.STRING)
  private LectureStatus status;

  @Builder
  private LectureEntity(
      String name,
      Part part,
      int generation,
      String place,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LectureAttribute attribute,
      LectureStatus status) {
    this.name = name;
    this.part = part;
    this.generation = generation;
    this.place = place;
    this.startDate = startDate;
    this.endDate = endDate;
    this.attribute = attribute;
    this.status = status;
  }
}
