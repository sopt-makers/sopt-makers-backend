package org.sopt.makers.domain.admin.attendance.port;

import java.util.List;

public interface AdminSubAttendanceLecturePort {

  void saveAllForAttendances(List<Long> attendanceIds, List<Long> subLectureIds);

  void deleteAllBySubLectureIds(List<Long> subLectureIds);
}
