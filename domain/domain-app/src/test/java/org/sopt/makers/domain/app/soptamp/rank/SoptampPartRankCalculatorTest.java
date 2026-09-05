package org.sopt.makers.domain.app.soptamp.rank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

@DisplayName("SoptampPartRankCalculator 테스트")
class SoptampPartRankCalculatorTest {

  private static final List<SoptampUser> USERS =
      List.of(
          user(10L, 100L, SoptampPart.SERVER),
          user(20L, 200L, SoptampPart.ANDROID),
          user(30L, 300L, SoptampPart.IOS),
          user(40L, 300L, SoptampPart.DESIGN),
          user(50L, 500L, SoptampPart.SERVER),
          user(60L, 600L, SoptampPart.SERVER));

  private static final Map<Part, Long> MEMBER_COUNTS =
      Map.of(
          Part.PLAN, 10L,
          Part.DESIGN, 10L,
          Part.WEB, 10L,
          Part.IOS, 10L,
          Part.ANDROID, 10L,
          Part.SERVER, 30L);

  @Test
  @DisplayName("응답은 점수순이 아니라 기-디-웹-아-안-서 고정 순서다")
  void returnsFixedPartOrder() {
    List<PartRank> ranks = SoptampPartRankCalculator.calculatePartRank(USERS, MEMBER_COUNTS);

    assertThat(ranks)
        .extracting(PartRank::part)
        .containsExactly("기획", "디자인", "웹", "아요", "안드", "서버");
  }

  @Test
  @DisplayName("평균은 소수 둘째 자리 HALF_UP 이고 동점은 공동 순위로 다음 순위를 건너뛴다")
  void ranksByAveragePointWithSharedRank() {
    List<PartRank> ranks = SoptampPartRankCalculator.calculatePartRank(USERS, MEMBER_COUNTS);

    assertThat(ranks)
        .extracting(PartRank::part, PartRank::rank, PartRank::pointsDecimal)
        .containsExactly(
            tuple("기획", 5, new BigDecimal("0.00")),
            tuple("디자인", 2, new BigDecimal("30.00")),
            tuple("웹", 5, new BigDecimal("0.00")),
            tuple("아요", 2, new BigDecimal("30.00")),
            tuple("안드", 4, new BigDecimal("20.00")),
            tuple("서버", 1, new BigDecimal("40.00")));
  }

  @Test
  @DisplayName("points 는 반올림이 아니라 소수부를 버린 값이다")
  void truncatesLegacyPoints() {
    List<PartRank> ranks =
        SoptampPartRankCalculator.calculatePartRank(
            List.of(user(10L, 100L, SoptampPart.SERVER)), Map.of(Part.SERVER, 3L));

    assertThat(ranks)
        .filteredOn(rank -> rank.part().equals("서버"))
        .singleElement()
        .extracting(PartRank::points, PartRank::pointsDecimal)
        .containsExactly(33L, new BigDecimal("33.33"));
  }

  @Test
  @DisplayName("인원이 없는 파트는 포트가 키를 주지 않아도 0.00 이다")
  void fallsBackToZeroWhenNoMember() {
    List<PartRank> ranks =
        SoptampPartRankCalculator.calculatePartRank(
            List.of(user(10L, 100L, SoptampPart.SERVER)), Map.of());

    assertThat(ranks)
        .extracting(PartRank::pointsDecimal)
        .containsOnly(new BigDecimal("0.00"))
        .hasSize(6);
  }

  @Test
  @DisplayName("파트장 점수는 분자에 들어가고 임원진과 챕터는 집계에서 빠진다")
  void includesPartLeaderAndExcludesOthers() {
    List<PartRank> ranks =
        SoptampPartRankCalculator.calculatePartRank(
            List.of(
                user(10L, 100L, SoptampPart.SERVER_PART_LEADER),
                user(20L, 900L, SoptampPart.PRESIDENT),
                user(30L, 900L, SoptampPart.PM),
                user(40L, 900L, null)),
            Map.of(Part.SERVER, 10L));

    assertThat(ranks)
        .extracting(PartRank::part, PartRank::pointsDecimal)
        .contains(tuple("서버", new BigDecimal("10.00")))
        .contains(tuple("기획", new BigDecimal("0.00")));
  }

  private static SoptampUser user(Long userId, Long totalPoints, SoptampPart part) {
    return new SoptampUser(userId, userId, "한마디", totalPoints, "유저" + userId, 38L, part);
  }
}
