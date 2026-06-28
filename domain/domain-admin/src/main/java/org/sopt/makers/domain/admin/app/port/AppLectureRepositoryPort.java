package org.sopt.makers.domain.admin.app.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.app.AppLecture;
import org.sopt.makers.domain.admin.app.AppSubLecture;

public interface AppLectureRepositoryPort {

  List<AppLecture> findTodayLectures(Long userId, int generation, Part part);

  Optional<AppLecture> findById(Long lectureId);

  Optional<AppSubLecture> findSubLectureByLectureIdAndRound(Long lectureId, int round);
}
