package org.sopt.makers.domain.admin.attendance.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public interface AttendanceRepositoryPort {

  Optional<Attendance> findById(Long id);

  Optional<Attendance> findByLectureIdAndUserId(Long lectureId, Long userId);

  List<Attendance> findAllByUserId(Long userId);

  List<Attendance> findAllEndedByUserId(Long userId, int generation);

  List<Attendance> findAllEndedByUserIds(List<Long> userIds, int generation);

  void updateStatus(Long attendanceId, AttendanceStatus status);

  List<Attendance> findAllByLectureIdAndPart(Long lectureId, Part part, int page, int limit);

  int countByLectureIdAndPart(Long lectureId, Part part);
}
