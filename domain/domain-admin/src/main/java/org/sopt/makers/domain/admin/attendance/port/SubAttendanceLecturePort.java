package org.sopt.makers.domain.admin.attendance.port;

import java.util.List;

public interface SubAttendanceLecturePort {

  void saveAllForAttendances(List<Long> attendanceIds, List<Long> subLectureIds);

  void deleteAllBySubLectureIds(List<Long> subLectureIds);
}
