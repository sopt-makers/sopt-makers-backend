package org.sopt.makers.domain.user.port;

import java.util.List;
import java.util.Optional;
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

  boolean existsByPhone(String phone);

  int countByGenerationAndIsSopt(int generation, boolean isSopt);

  User save(User user);

  Page<User> findPageByCondition(
      UserSearchCondition condition, Pageable pageable, UserSortType sortType);
}
