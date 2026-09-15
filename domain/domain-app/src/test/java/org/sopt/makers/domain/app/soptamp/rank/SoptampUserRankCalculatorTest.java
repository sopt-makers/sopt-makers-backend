package org.sopt.makers.domain.app.soptamp.rank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

@DisplayName("SoptampUserRankCalculator 테스트")
class SoptampUserRankCalculatorTest {

  @Test
  @DisplayName("점수 내림차순으로 전원에게 1부터 일련번호를 붙인다")
  void ranksAllUsersByScoreDesc() {
    List<UserRank> ranks =
        SoptampUserRankCalculator.calculateRank(
            List.of(user(1L, "서버유저", 100L), user(2L, "안드유저", 300L), user(3L, "아요유저", 200L)));

    assertThat(ranks)
        .extracting(UserRank::rank, UserRank::nickname, UserRank::point)
        .containsExactly(tuple(1, "안드유저", 300L), tuple(2, "아요유저", 200L), tuple(3, "서버유저", 100L));
  }

  @Test
  @DisplayName("동점이어도 공동 순위 없이 일련번호가 이어진다")
  void doesNotShareRankOnTie() {
    List<UserRank> ranks =
        SoptampUserRankCalculator.calculateRank(
            List.of(user(1L, "아요유저", 300L), user(2L, "디자인유저", 300L), user(3L, "서버유저", 100L)));

    assertThat(ranks).extracting(UserRank::rank).containsExactly(1, 2, 3);
  }

  @Test
  @DisplayName("자르는 인원 수 제한이 없어 전원이 나온다")
  void doesNotLimitSize() {
    List<SoptampUser> users =
        LongStream.rangeClosed(1, 50).mapToObj(id -> user(id, "유저" + id, id)).toList();

    assertThat(SoptampUserRankCalculator.calculateRank(users)).hasSize(50);
  }

  @Test
  @DisplayName("프로필 메시지를 그대로 옮긴다")
  void copiesProfileMessage() {
    List<UserRank> ranks = SoptampUserRankCalculator.calculateRank(List.of(user(1L, "서버유저", 10L)));

    assertThat(ranks).singleElement().extracting(UserRank::profileMessage).isEqualTo("한마디");
  }

  private static SoptampUser user(Long userId, String nickname, Long totalPoints) {
    return new SoptampUser(userId, userId, "한마디", totalPoints, nickname, 38L, SoptampPart.SERVER);
  }
}
