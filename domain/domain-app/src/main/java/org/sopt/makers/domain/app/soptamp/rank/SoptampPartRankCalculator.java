package org.sopt.makers.domain.app.soptamp.rank;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public final class SoptampPartRankCalculator {

  private static final int POINT_SCALE = 2;
  private static final RoundingMode POINT_ROUNDING_MODE = RoundingMode.HALF_UP;
  private static final BigDecimal ZERO_POINT =
      BigDecimal.ZERO.setScale(POINT_SCALE, POINT_ROUNDING_MODE);

  private static final List<Part> RANKING_PARTS =
      List.of(Part.PLAN, Part.DESIGN, Part.WEB, Part.IOS, Part.ANDROID, Part.SERVER);

  private SoptampPartRankCalculator() {}

  public static List<PartRank> calculatePartRank(
      List<SoptampUser> users, Map<Part, Long> partMemberCounts) {
    Map<Part, Long> partScores = calculatePartScores(users);
    Map<Part, BigDecimal> averagePoints = calculateAveragePoints(partScores, partMemberCounts);
    Map<Part, Integer> ranks = calculateRanks(averagePoints);

    return RANKING_PARTS.stream()
        .map(
            part -> {
              // TODO: 파트 랭킹 조회시 기존(points) 정수를 유지하고 신규(pointsDecimal)을 추가함으로 앱 하위 호환 대응. 추후 points
              // 제거 필요.
              BigDecimal pointsDecimal = averagePoints.get(part);

              return new PartRank(
                  SoptampPart.of(part).getShortName(),
                  ranks.get(part),
                  pointsDecimal.longValue(),
                  pointsDecimal);
            })
        .toList();
  }

  private static Map<Part, Long> calculatePartScores(List<SoptampUser> users) {
    Map<Part, Long> partScores =
        users.stream()
            .filter(user -> toRankingPart(user) != null)
            .collect(
                groupingBy(
                    SoptampPartRankCalculator::toRankingPart,
                    () -> new EnumMap<>(Part.class),
                    summingLong(SoptampUser::totalPoints)));

    RANKING_PARTS.forEach(part -> partScores.putIfAbsent(part, 0L));

    return partScores;
  }

  private static Part toRankingPart(SoptampUser user) {
    return user.part() == null ? null : user.part().toPart();
  }

  private static Map<Part, BigDecimal> calculateAveragePoints(
      Map<Part, Long> partScores, Map<Part, Long> partMemberCounts) {
    Map<Part, BigDecimal> averagePoints = new EnumMap<>(Part.class);

    for (Part part : RANKING_PARTS) {
      long totalScore = partScores.getOrDefault(part, 0L);
      long memberCount = partMemberCounts.getOrDefault(part, 0L);

      BigDecimal averagePoint =
          memberCount == 0
              ? ZERO_POINT
              : BigDecimal.valueOf(totalScore)
                  .divide(BigDecimal.valueOf(memberCount), POINT_SCALE, POINT_ROUNDING_MODE);

      averagePoints.put(part, averagePoint);
    }

    return averagePoints;
  }

  private static Map<Part, Integer> calculateRanks(Map<Part, BigDecimal> averagePoints) {
    List<Entry<Part, BigDecimal>> sortedParts =
        averagePoints.entrySet().stream()
            .sorted(comparingByValue(Comparator.reverseOrder()))
            .toList();

    Map<Part, Integer> ranks = new EnumMap<>(Part.class);
    BigDecimal previousPoint = null;
    int currentRank = 0;

    for (int i = 0; i < sortedParts.size(); i++) {
      Entry<Part, BigDecimal> entry = sortedParts.get(i);

      if (previousPoint == null || entry.getValue().compareTo(previousPoint) != 0) {
        currentRank = i + 1;
        previousPoint = entry.getValue();
      }

      ranks.put(entry.getKey(), currentRank);
    }

    return ranks;
  }
}
