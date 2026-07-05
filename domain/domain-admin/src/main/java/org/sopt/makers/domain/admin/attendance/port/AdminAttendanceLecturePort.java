package org.sopt.makers.domain.admin.attendance.port;

import java.util.List;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public interface AdminAttendanceLecturePort {

  List<Long> saveAllForUsers(Long lectureId, List<Long> userIds);

  List<Long> getUserIdsByLectureId(Long lectureId);

  List<Long> getAttendanceIdsByLectureId(Long lectureId);

  int countByLectureIdAndStatus(Long lectureId, AttendanceStatus status);

  void deleteByLectureId(Long lectureId);
}
