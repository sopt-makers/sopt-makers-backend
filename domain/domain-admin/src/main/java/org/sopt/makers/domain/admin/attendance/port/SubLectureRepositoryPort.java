package org.sopt.makers.domain.admin.attendance.port;

import java.util.Optional;
import org.sopt.makers.domain.admin.attendance.SubLecture;

public interface SubLectureRepositoryPort {

  Optional<SubLecture> findById(Long id);
}
