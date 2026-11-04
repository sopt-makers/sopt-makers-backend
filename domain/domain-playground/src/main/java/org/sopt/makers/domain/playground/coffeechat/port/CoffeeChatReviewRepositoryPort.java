package org.sopt.makers.domain.playground.coffeechat.port;

import java.util.List;
import org.sopt.makers.domain.playground.coffeechat.CoffeeChatReview;

public interface CoffeeChatReviewRepositoryPort {

  void save(
      Long reviewerId,
      Long coffeeChatId,
      Long anonymousProfileImageId,
      String nickname,
      String content);

  boolean existsByReviewerIdAndCoffeeChatId(Long reviewerId, Long coffeeChatId);

  List<CoffeeChatReview> findTop6ByOrderByIdDesc();
}
