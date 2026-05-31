package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.attendance.SubLecture;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "sub_lectures")
public class SubLectureEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lecture_id", nullable = false)
  private LectureEntity lecture;

  private int round;

  private LocalDateTime startAt;

  private String code;

  public SubLecture toDomain() {
    return new SubLecture(
        id, lecture.getId(), lecture.getAttribute(), lecture.getGeneration(), round, startAt, code);
  }
}
