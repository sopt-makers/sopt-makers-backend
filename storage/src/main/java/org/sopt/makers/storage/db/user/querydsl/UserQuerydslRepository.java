package org.sopt.makers.storage.db.user.querydsl;

import java.util.Set;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQuerydslRepository {

  Page<Long> findUserIdsByCondition(
      UserSearchCondition condition, Pageable pageable, UserSortType sortType);

  Set<Long> findUserIdsByRecommendCondition(
      Set<Integer> generations, String mbti, String university);
}
