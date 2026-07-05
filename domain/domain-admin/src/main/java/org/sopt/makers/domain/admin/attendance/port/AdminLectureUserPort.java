package org.sopt.makers.domain.admin.attendance.port;

import java.util.List;
import org.sopt.makers.core.type.Part;

public interface AdminLectureUserPort {

  List<Long> findUserIdsByGenerationAndPart(int generation, Part part);
}
