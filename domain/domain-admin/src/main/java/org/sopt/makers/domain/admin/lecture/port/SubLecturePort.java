package org.sopt.makers.domain.admin.lecture.port;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.admin.lecture.SubLecture;

public interface SubLecturePort {

  void saveAll(Long lectureId, List<Integer> rounds);

  List<SubLecture> findAllByLectureId(Long lectureId);

  void updateCodeAndStartAt(Long subLectureId, String code, LocalDateTime startAt);

  void deleteAllByLectureId(Long lectureId);
}
