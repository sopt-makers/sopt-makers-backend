package org.sopt.makers.domain.app.soptamp.rank;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public final class SoptampUserRankCalculator {

  private SoptampUserRankCalculator() {}

  public static List<UserRank> calculateRank(List<SoptampUser> users) {
    AtomicInteger rankPoint = new AtomicInteger(1);

    return users.stream()
        .sorted(Comparator.comparing(SoptampUser::totalPoints).reversed())
        .map(
            user ->
                new UserRank(
                    rankPoint.getAndIncrement(),
                    user.nickname(),
                    user.totalPoints(),
                    user.profileMessage()))
        .toList();
  }
}
