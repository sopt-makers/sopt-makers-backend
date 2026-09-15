package org.sopt.makers.storage.db.user.querydsl;

import java.util.List;
import java.util.Set;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQuerydslRepository {

  Page<Long> findUserIdsByCondition(
      UserSearchCondition condition, Pageable pageable, UserSortType sortType);

  Set<Long> findUserIdsByRecommendCondition(
      Set<Integer> generations, String mbti, String university);

  List<Long> findUserIdsWithProfileByMbtiAndEmployed(String mbti, Boolean employed);

  /** 프로필이 있는 유저 중 generation/part 조건(둘 다 nullable)의 활동을 가진 유저 id 목록. */
  List<Long> findUserIdsWithProfileByActivity(Integer generation, Part part, boolean isSopt);

  /** 프로필이 있는 유저 중 mbti가 일치하는 유저 id 목록. */
  List<Long> findUserIdsWithProfileByMbti(String mbti);

  /** 프로필이 있는 유저 중 university가 일치하는 유저 id 목록. */
  List<Long> findUserIdsWithProfileByUniversity(String university);

  /** 프로필이 있는 유저 중 작업 성향이 설정된 유저 id 목록. */
  List<Long> findUserIdsWithProfileAndWorkPreference();
}
