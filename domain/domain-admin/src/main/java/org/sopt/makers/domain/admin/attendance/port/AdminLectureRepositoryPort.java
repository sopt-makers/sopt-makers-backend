package org.sopt.makers.domain.admin.attendance.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AdminLecture;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.domain.admin.attendance.LectureStatus;

public interface AdminLectureRepositoryPort {

  AdminLecture save(
      String name,
      Part part,
      int generation,
      String place,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LectureAttribute attribute,
      LectureStatus status);

  Optional<AdminLecture> findById(Long lectureId);

  List<AdminLecture> findAllByGenerationAndPart(int generation, Part part);

  void updateStatus(Long lectureId, LectureStatus status);

  void deleteById(Long lectureId);
}
