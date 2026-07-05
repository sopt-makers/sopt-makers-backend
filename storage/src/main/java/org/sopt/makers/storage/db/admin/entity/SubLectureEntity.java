package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.makers.domain.admin.attendance.SubLecture;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "sub_lectures",
    uniqueConstraints = @UniqueConstraint(columnNames = {"lecture_id", "round"}))
public class SubLectureEntity extends BaseEntity {

  @Id
  @Column(name = "sub_lecture_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lecture_id", nullable = false)
  private LectureEntity lecture;

  private int round;

  private LocalDateTime startAt;

  private String code;

  public SubLecture toDomain() {
    return new SubLecture(
        getId(),
        lecture.getId(),
        lecture.getAttribute(),
        lecture.getGeneration(),
        round,
        startAt,
        code);
  }

  public static SubLectureEntity create(LectureEntity lecture, int round) {
    SubLectureEntity entity = new SubLectureEntity();
    entity.lecture = lecture;
    entity.round = round;
    return entity;
  }

  public void updateCodeAndStartAt(String newCode, LocalDateTime newStartAt) {
    this.code = newCode;
    this.startAt = newStartAt;
  }
}
