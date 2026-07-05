package org.sopt.makers.storage.db.user.querydsl;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.springframework.data.domain.Pageable;

public interface UserActivityHistoryQuerydslRepository {

  List<UserActivityHistoryEntity> findByGenerationAndPartWithUser(
      int generation, Part part, Pageable pageable);

  int countByGenerationAndPart(int generation, Part part);

  List<Long> findUserIdsByGenerationAndPart(int generation, Part part);
}
