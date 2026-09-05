package org.sopt.makers.domain.user.port;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.sopt.makers.domain.user.PokeUserProfile;
import org.sopt.makers.domain.user.RecommendSeed;

public interface AppPokeUserPort {

  boolean exists(Long userId);

  List<Long> filterExisting(Collection<Long> userIds);

  List<Long> findAllUserIds();

  PokeUserProfile findProfile(Long userId);

  List<PokeUserProfile> findProfiles(Collection<Long> userIds);

  RecommendSeed findRecommendSeed(Long userId);

  Set<Long> findUserIdsByRecommendCondition(
      Set<Integer> generations, String mbti, String university);
}
