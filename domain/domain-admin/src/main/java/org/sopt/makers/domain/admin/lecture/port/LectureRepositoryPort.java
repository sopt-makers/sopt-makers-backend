package org.sopt.makers.domain.admin.lecture.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;

public interface LectureRepositoryPort {

  Lecture save(
      String name,
      Part part,
      int generation,
      String place,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LectureAttribute attribute,
      LectureStatus status);

  Optional<Lecture> findById(Long lectureId);

  List<Lecture> findAllByGenerationAndPart(int generation, Part part);

  void updateStatus(Long lectureId, LectureStatus status);

  void deleteById(Long lectureId);
}
