package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "attendances")
public class AttendanceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        id,
        userId,
        lecture.getId(),
        lecture.getAttribute(),
        lecture.getStatus() == LectureStatus.END,
        status,
        subAttendances);
  }
}
