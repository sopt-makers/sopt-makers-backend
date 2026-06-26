package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "sub_attendances")
public class SubAttendanceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attendance_id", nullable = false)
  private AttendanceEntity attendance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_lecture_id", nullable = false)
  private SubLectureEntity subLecture;

  @Enumerated(EnumType.STRING)
  private AttendanceStatus status;

  public void updateStatus(AttendanceStatus status) {
    this.status = status;
  }

  public SubAttendance toDomain() {
    return new SubAttendance(
        id,
        attendance.getId(),
        subLecture.getId(),
        subLecture.getRound(),
        subLecture.getStartAt(),
        status,
        null);
  }

  public static SubAttendanceEntity fromDomain(
      SubAttendance subAttendance,
      AttendanceEntity attendanceEntity,
      SubLectureEntity subLectureEntity) {
    SubAttendanceEntity entity = new SubAttendanceEntity();
    entity.attendance = attendanceEntity;
    entity.subLecture = subLectureEntity;
    entity.status = subAttendance.status();
    return entity;
  }
}
