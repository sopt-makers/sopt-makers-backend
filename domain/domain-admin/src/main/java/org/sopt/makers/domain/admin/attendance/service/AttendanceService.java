package org.sopt.makers.domain.admin.attendance.service;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureAttendanceDetail;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.UserAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.exception.LectureException;
import org.sopt.makers.domain.admin.lecture.exception.LectureFailure;
import org.sopt.makers.domain.admin.lecture.port.SubLectureRepositoryPort;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AttendanceService {

  private final SubLectureRepositoryPort subLectureRepositoryPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;
  private final SubAttendanceRepositoryPort subAttendanceRepositoryPort;
  private final AdminUserActivityPort adminUserActivityPort;
  private final int currentGeneration;

  public AttendanceService(
      SubLectureRepositoryPort subLectureRepositoryPort,
      AttendanceRepositoryPort attendanceRepositoryPort,
      SubAttendanceRepositoryPort subAttendanceRepositoryPort,
      AdminUserActivityPort adminUserActivityPort,
      @Value("${sopt.current.generation}") int currentGeneration) {
    this.subLectureRepositoryPort = subLectureRepositoryPort;
    this.attendanceRepositoryPort = attendanceRepositoryPort;
    this.subAttendanceRepositoryPort = subAttendanceRepositoryPort;
    this.adminUserActivityPort = adminUserActivityPort;
    this.currentGeneration = currentGeneration;
  }

  public UserAttendanceSummary getAttendancesByUserId(Long userId) {
    Member member = findMember(userId);
    return new UserAttendanceSummary(
        member,
        attendanceRepositoryPort.findAllDetailsByUserIdAndGeneration(userId, currentGeneration));
  }

  private Member findMember(Long userId) {
    return adminUserActivityPort
        .findMemberByUserIdAndGeneration(userId, currentGeneration)
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));
  }

  public List<LectureAttendanceDetail> getAttendancesByLectureId(
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
  public SubAttendance updateSubAttendance(Long subAttendanceId, AttendanceStatus status) {
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
    return updated;
  }

  @Transactional
  public float updateAttendanceScore(Long userId) {
    findMember(userId);
    List<Attendance> endedAttendances =
        attendanceRepositoryPort.findAllEndedByUserId(userId, currentGeneration);

    float totalScore = Attendance.computeTotalScore(endedAttendances);

    adminUserActivityPort.updateAttendanceScore(userId, currentGeneration, totalScore);
    return totalScore;
  }
}
