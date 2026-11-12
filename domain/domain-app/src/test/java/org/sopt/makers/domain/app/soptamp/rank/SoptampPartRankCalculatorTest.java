package org.sopt.makers.domain.app.soptamp.rank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

  @ParameterizedTest(name = "총점 {0}, 인원 {1} → {2}")
  @CsvSource({"1000, 25, 20000.00", "900, 9, 30000.00", "100, 2, 7071.00"})
  @DisplayName("점수는 총점을 √인원으로 나눠 소수 둘째 자리 HALF_UP 한 뒤 100을 곱한 값이다")
  void calculatesAdjustedPoint(long totalPoints, long memberCount, BigDecimal expected) {
    assertThat(serverPointOf(totalPoints, memberCount)).isEqualTo(expected);
  }

  @ParameterizedTest(name = "총점 {0}, 인원 {1} → {2}")
  @CsvSource({"500, 0, 0.00", "0, 10, 0.00", "100, 1, 10000.00", "1001, 64, 12513.00"})
  @DisplayName("인원 0명과 총점 0은 0점이고 인원 1명은 총점 그대로이며 셋째 자리 5는 올린다")
  void calculatesAdjustedPointOnBoundary(long totalPoints, long memberCount, BigDecimal expected) {
    assertThat(serverPointOf(totalPoints, memberCount)).isEqualTo(expected);
  }

  @Test
  @DisplayName("동점은 공동 순위이고 다음 순위는 동점 파트 수만큼 건너뛴다")
  void ranksByAdjustedPointWithSharedRank() {
    List<PartRank> ranks = SoptampPartRankCalculator.calculatePartRank(USERS, MEMBER_COUNTS);

    assertThat(ranks)
        .extracting(PartRank::part, PartRank::rank, PartRank::pointsDecimal)
        .containsExactly(
            tuple("기획", 5, new BigDecimal("0.00")),
            tuple("디자인", 2, new BigDecimal("9487.00")),
            tuple("웹", 5, new BigDecimal("0.00")),
            tuple("아요", 2, new BigDecimal("9487.00")),
            tuple("안드", 4, new BigDecimal("6325.00")),
            tuple("서버", 1, new BigDecimal("21909.00")));
  }

  @Test
  @DisplayName("순위는 1인 평균이 아니라 총점을 √인원으로 나눈 값 기준이다")
  void ranksByAdjustedPointNotByAverage() {
    List<PartRank> ranks =
        SoptampPartRankCalculator.calculatePartRank(
            List.of(
                user(10L, 1100L, SoptampPart.PLAN),
                user(20L, 1500L, SoptampPart.DESIGN),
                user(30L, 2880L, SoptampPart.SERVER)),
            Map.of(Part.PLAN, 11L, Part.DESIGN, 16L, Part.SERVER, 32L));

    assertThat(ranks)
        .extracting(PartRank::part, PartRank::rank, PartRank::pointsDecimal)
        .containsExactly(
            tuple("기획", 3, new BigDecimal("33166.00")),
            tuple("디자인", 2, new BigDecimal("37500.00")),
            tuple("웹", 4, new BigDecimal("0.00")),
            tuple("아요", 4, new BigDecimal("0.00")),
            tuple("안드", 4, new BigDecimal("0.00")),
            tuple("서버", 1, new BigDecimal("50912.00")));
  }

  @Test
  @DisplayName("인원이 다른 파트라도 점수가 같으면 공동 순위다")
  void sharesRankAcrossDifferentMemberCounts() {
    List<PartRank> ranks =
        SoptampPartRankCalculator.calculatePartRank(
            List.of(
                user(10L, 200L, SoptampPart.PLAN),
                user(20L, 300L, SoptampPart.DESIGN),
                user(30L, 320L, SoptampPart.SERVER)),
            Map.of(Part.PLAN, 4L, Part.DESIGN, 9L, Part.SERVER, 16L));

    assertThat(ranks)
        .extracting(PartRank::part, PartRank::rank, PartRank::pointsDecimal)
        .containsExactly(
            tuple("기획", 1, new BigDecimal("10000.00")),
            tuple("디자인", 1, new BigDecimal("10000.00")),
            tuple("웹", 4, new BigDecimal("0.00")),
            tuple("아요", 4, new BigDecimal("0.00")),
            tuple("안드", 4, new BigDecimal("0.00")),
            tuple("서버", 3, new BigDecimal("8000.00")));
  }

  @Test
  @DisplayName("반올림 전에는 달라도 소수 둘째 자리까지 반올림한 값이 같으면 공동 순위다")
  void sharesRankWhenRoundedPointsAreEqual() {
    List<PartRank> ranks =
        SoptampPartRankCalculator.calculatePartRank(
            List.of(user(10L, 1L, SoptampPart.PLAN), user(20L, 1L, SoptampPart.DESIGN)),
            Map.of(Part.PLAN, 17L, Part.DESIGN, 18L));

    assertThat(ranks)
        .extracting(PartRank::part, PartRank::rank, PartRank::pointsDecimal)
        .containsExactly(
            tuple("기획", 1, new BigDecimal("24.00")),
            tuple("디자인", 1, new BigDecimal("24.00")),
            tuple("웹", 3, new BigDecimal("0.00")),
            tuple("아요", 3, new BigDecimal("0.00")),
            tuple("안드", 3, new BigDecimal("0.00")),
            tuple("서버", 3, new BigDecimal("0.00")));
  }

  @Test
  @DisplayName("모든 파트 점수가 0이면 여섯 파트 모두 공동 1위다")
  void sharesFirstRankWhenAllPointsAreZero() {
    List<PartRank> ranks = SoptampPartRankCalculator.calculatePartRank(List.of(), MEMBER_COUNTS);

    assertThat(ranks)
        .extracting(PartRank::rank, PartRank::pointsDecimal)
        .containsOnly(tuple(1, new BigDecimal("0.00")))
        .hasSize(6);
  }

  @Test
  @DisplayName("points 는 100을 곱한 pointsDecimal 과 같은 정수 값이다")
  void legacyPointsMatchesScaledPointsDecimal() {
    List<PartRank> ranks =
        SoptampPartRankCalculator.calculatePartRank(
            List.of(user(10L, 100L, SoptampPart.SERVER)), Map.of(Part.SERVER, 3L));

    assertThat(ranks)
        .filteredOn(rank -> rank.part().equals("서버"))
        .singleElement()
        .extracting(PartRank::points, PartRank::pointsDecimal)
        .containsExactly(5774L, new BigDecimal("5774.00"));
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
        .contains(tuple("서버", new BigDecimal("3162.00")))
        .contains(tuple("기획", new BigDecimal("0.00")));
  }

  private static BigDecimal serverPointOf(long totalPoints, long memberCount) {
    return SoptampPartRankCalculator.calculatePartRank(
            List.of(user(10L, totalPoints, SoptampPart.SERVER)), Map.of(Part.SERVER, memberCount))
        .stream()
        .filter(rank -> rank.part().equals("서버"))
        .findFirst()
        .orElseThrow()
        .pointsDecimal();
  }

  private static SoptampUser user(Long userId, Long totalPoints, SoptampPart part) {
    return new SoptampUser(userId, userId, "한마디", totalPoints, "유저" + userId, 38L, part);
  }
}
