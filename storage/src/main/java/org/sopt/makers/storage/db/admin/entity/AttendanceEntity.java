package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "attendances")
public class AttendanceEntity extends BaseEntity {

  @Id
  @Column(name = "attendance_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lecture_id", nullable = false)
  private LectureEntity lecture;

  @Enumerated(EnumType.STRING)
  private AttendanceStatus status;

  public Attendance toDomain(List<SubAttendance> subAttendances) {
    return new Attendance(
        getId(),
        userId,
        lecture.getId(),
        lecture.getName(),
        lecture.getStartDate(),
        lecture.getAttribute(),
        lecture.getStatus() == LectureStatus.END,
        status,
        subAttendances);
  }
}
