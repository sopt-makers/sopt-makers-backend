package org.sopt.makers.domain.app.home.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.rank.PartRank;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.sopt.makers.domain.app.soptamp.service.SoptampUserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoptampBadgeManager implements AppServiceBadgeManager {

  private static final String RANK_SUFFIX = "위";

  private final SoptampUserService soptampUserService;
  private final RankService rankService;
  private final SoptampMode soptampMode;

  @Override
  public AppServiceBadgeInfo acquireAppServiceBadgeInfo(Long userId) {
    if (soptampMode.isAppjam()) {
      return AppServiceBadgeInfo.createWithAllDisabled();
    }
    SoptampUser soptampUser = soptampUserService.getSoptampUser(userId);
    Part part = soptampUser.part() == null ? null : soptampUser.part().toPart();
    if (part == null) {
      return AppServiceBadgeInfo.createWithAllDisabled();
    }
    return AppServiceBadgeInfo.createWithEnabledDisplayAlarmBadge(findPartRank(part) + RANK_SUFFIX);
  }

  private Integer findPartRank(Part part) {
    String shortName = SoptampPart.of(part).getShortName();
    return rankService.findAllPartRanks().stream()
        .filter(partRank -> partRank.part().equals(shortName))
        .map(PartRank::rank)
        .findFirst()
        .orElseThrow();
  }
}
