package org.sopt.makers.domain.user.port;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.WorkPreference;

public interface PlaygroundRecommendationUserPort {

  /** 프로필이 있는 유저 중 generation/part 조건(둘 다 nullable)의 활동을 가진 유저 id 목록. */
  List<Long> findUserIdsByActivity(Integer generation, Part part, boolean isSopt);

  /** 프로필이 있는 유저 중 mbti가 일치하는 유저 id 목록. */
  List<Long> findUserIdsByMbti(String mbti);

  /** 프로필이 있는 유저 중 university가 일치하는 유저 id 목록. */
  List<Long> findUserIdsByUniversity(String university);

  /** 프로필이 있는 유저 중 작업 성향이 설정된 유저 id 목록. */
  List<Long> findUserIdsWithWorkPreference();

  /** 추천 카드 구성에 필요한 유저 정보를 벌크 조회한다. 프로필이 없는 유저는 결과에서 제외한다. */
  List<RecommendationUserInfo> findRecommendationUserInfosByIds(List<Long> userIds);

  record RecommendationUserInfo(
      Long id,
      String name,
      String profileImage,
      String university,
      String mbti,
      WorkPreference workPreference,
      List<Activity> activities) {}
}
