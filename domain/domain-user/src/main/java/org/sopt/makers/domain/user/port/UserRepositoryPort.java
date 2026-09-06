package org.sopt.makers.domain.user.port;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryPort {

  Optional<User> findById(Long userId);

  Optional<User> findWithActivitiesById(Long userId);

  Optional<User> findBySocialAccount(OAuthPlatform platformType, String platformId);

  Optional<User> findByPhone(String phone);

  List<User> findAllWithActivitiesByIds(List<Long> userIds);

  List<Long> findAllUserIds();

  /**
   * 프로필이 있는(isFirstLogin=false) 유저 중 mbti/재직여부 DB 필터를 만족하는 id 목록을 반환한다. mbti, employed가 각각 null이면
   * 해당 조건은 적용하지 않는다.
   */
  List<Long> findAllUserIdsWithProfileByMbtiAndEmployed(String mbti, Boolean employed);

  boolean existsByPhone(String phone);

  int countByGenerationAndIsSopt(int generation, boolean isSopt);

  User save(User user);

  Page<User> findPageByCondition(
      UserSearchCondition condition, Pageable pageable, UserSortType sortType);

  List<Long> filterExistingIds(Collection<Long> userIds);

  Set<Long> findUserIdsByRecommendCondition(
      Set<Integer> generations, String mbti, String university);
}
