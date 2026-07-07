package org.sopt.makers.domain.admin.attendance.port;

import java.util.List;
import java.util.Map;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public interface AttendanceLecturePort {

  List<Long> saveAllForUsers(Long lectureId, List<Long> userIds);

  List<Long> getUserIdsByLectureId(Long lectureId);

  List<Long> getAttendanceIdsByLectureId(Long lectureId);

  Map<Long, Map<AttendanceStatus, Integer>> countByLectureIdsGroupByStatus(List<Long> lectureIds);

  void deleteByLectureId(Long lectureId);
}
