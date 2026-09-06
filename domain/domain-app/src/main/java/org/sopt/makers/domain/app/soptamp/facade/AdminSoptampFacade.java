package org.sopt.makers.domain.app.soptamp.facade;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapMilestonePort;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapRepositoryPort;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampImageDeletePort;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.sopt.makers.domain.app.soptamp.service.SoptampUserService;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminSoptampFacade {

  private final ClapMilestonePort clapMilestonePort;
  private final ClapRepositoryPort clapRepositoryPort;
  private final StampRepositoryPort stampRepositoryPort;
  private final SoptampImageDeletePort soptampImageDeletePort;
  private final SoptampUserService soptampUserService;
  private final RankService rankService;
  private final SoptampMode soptampMode;

  @Transactional
  public void clearSoptampData(boolean stamp, boolean soptampUser) {
    if (stamp) {
      clapMilestonePort.deleteAll();
      clapRepositoryPort.deleteAll();
      deleteAllStampsWithImages();
    }
    if (soptampUser) {
      soptampUserService.deleteAllSoptampUsers();
    }
  }

  @Transactional
  public void initPoints() {
    soptampUserService.initAllPoints();
    rankService.reloadRankCache();
  }

  public void initRankCache() {
    if (soptampMode.isAppjam()) {
      throw new SoptampException(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST);
    }
    rankService.reloadRankCache();
  }

  private void deleteAllStampsWithImages() {
    List<String> imageUrls =
        stampRepositoryPort.findAll().stream()
            .map(Stamp::images)
            .flatMap(Collection::stream)
            .toList();

    stampRepositoryPort.deleteAll();

    if (!imageUrls.isEmpty()) {
      soptampImageDeletePort.deleteAll(imageUrls);
    }
  }
}
