package org.sopt.makers.domain.admin.lecture.port;

import java.util.Optional;
import org.sopt.makers.domain.admin.lecture.SubLecture;

public interface SubLectureRepositoryPort {

  Optional<SubLecture> findById(Long id);
}
