package org.sopt.makers.domain.admin.user.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.user.AdminUser;
import org.sopt.makers.domain.admin.user.UserActivity;

public interface AdminUserActivityPort {

  List<AdminUser> findByGenerationAndPart(int generation, Part part, int page, int limit);

  int countByGenerationAndPart(int generation, Part part);

  List<Long> findUserIdsByGenerationAndPart(int generation, Part part);

  void updateAttendanceScore(Long userId, int generation, Float score);

  void bulkUpdateAttendanceScores(int generation, Map<Long, Float> userScores);

  Optional<UserActivity> findCurrentActivity(Long userId);
}
