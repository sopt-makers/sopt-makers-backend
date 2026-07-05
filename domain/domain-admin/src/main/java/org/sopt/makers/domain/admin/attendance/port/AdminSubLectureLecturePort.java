package org.sopt.makers.domain.admin.attendance.port;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.admin.attendance.SubLecture;

public interface AdminSubLectureLecturePort {

  void saveAll(Long lectureId, List<Integer> rounds);

  List<SubLecture> findAllByLectureId(Long lectureId);

  void updateCodeAndStartAt(Long subLectureId, String code, LocalDateTime startAt);

  void deleteAllByLectureId(Long lectureId);
}
