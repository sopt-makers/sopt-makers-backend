package org.sopt.makers.domain.admin.attendance.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.exception.LectureException;
import org.sopt.makers.domain.admin.lecture.exception.LectureFailure;
import org.sopt.makers.domain.admin.lecture.port.SubLectureRepositoryPort;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

  private static final float BASE_ATTENDANCE_SCORE = 2.0f;

  private final SubLectureRepositoryPort subLectureRepositoryPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;
  private final SubAttendanceRepositoryPort subAttendanceRepositoryPort;
  private final AdminUserActivityPort adminUserActivityPort;

  public List<Attendance> getAttendancesByUserId(Long userId) {
    return attendanceRepositoryPort.findAllByUserId(userId);
  }

  public List<Attendance> getAttendancesByLectureId(
      Long lectureId, Part part, int page, int limit) {
    return attendanceRepositoryPort.findAllByLectureIdAndPart(lectureId, part, page, limit);
  }

  public int countAttendancesByLecture(Long lectureId, Part part) {
    return attendanceRepositoryPort.countByLectureIdAndPart(lectureId, part);
  }

  @Transactional
  public void attend(Long userId, Long subLectureId, String code) {
    SubLecture subLecture =
        subLectureRepositoryPort
            .findById(subLectureId)
            .orElseThrow(() -> new LectureException(LectureFailure.NOT_FOUND_SUB_LECTURE));

    subLecture.validateForAttendance(code);

    Attendance attendance =
        attendanceRepositoryPort
            .findByLectureIdAndUserId(subLecture.lectureId(), userId)
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));

    SubAttendance subAttendance = attendance.getSubAttendanceByRound(subLecture.round());
    SubAttendance marked = subAttendance.markAttendance(LocalDateTime.now());

    subAttendanceRepositoryPort.save(marked);

    AttendanceStatus newStatus = attendance.computeStatus(marked);
    attendanceRepositoryPort.updateStatus(attendance.id(), newStatus);
  }

  @Transactional
  public void updateSubAttendance(Long subAttendanceId, AttendanceStatus status) {
    SubAttendance subAttendance =
        subAttendanceRepositoryPort
            .findById(subAttendanceId)
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_SUB_ATTENDANCE));

    SubAttendance updated = subAttendance.withStatus(status);
    subAttendanceRepositoryPort.save(updated);

    Attendance attendance =
        attendanceRepositoryPort
            .findById(updated.attendanceId())
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));

    AttendanceStatus newStatus = attendance.computeStatus(updated);
    attendanceRepositoryPort.updateStatus(attendance.id(), newStatus);
  }

  @Transactional
  public void updateAttendanceScore(Long userId, int generation) {
    List<Attendance> endedAttendances =
        attendanceRepositoryPort.findAllEndedByUserId(userId, generation);

    float totalScore =
        BASE_ATTENDANCE_SCORE
            + (float) endedAttendances.stream().mapToDouble(Attendance::computeScore).sum();

    adminUserActivityPort.updateAttendanceScore(userId, generation, totalScore);
  }
}
