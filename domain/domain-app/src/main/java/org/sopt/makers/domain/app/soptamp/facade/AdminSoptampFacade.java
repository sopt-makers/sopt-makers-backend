package org.sopt.makers.domain.app.soptamp.facade;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.app.soptamp.SoptampDeepLinkBuilder;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapMilestonePort;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapRepositoryPort;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.port.SoptampImageDeletePort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.sopt.makers.domain.app.soptamp.service.MissionService;
import org.sopt.makers.domain.app.soptamp.service.SoptampUserService;
import org.sopt.makers.domain.app.soptamp.service.StampService;
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
  private final SoptampUserQueryPort soptampUserQueryPort;
  private final MissionService missionService;
  private final PushSenderPort pushSenderPort;
  private final StampService stampService;

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

  public void sendSoptampShowcase(
      Long missionId, String nickname, String notificationTitle, String notificationContent) {
    SoptampUser owner =
        soptampUserQueryPort
            .findByNickname(nickname)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
    SoptampPart ownerPart = owner.part() == null ? SoptampPart.NONE : owner.part();
    Mission mission = missionService.getById(missionId);
    Stamp stamp = stampService.findStamp(missionId, owner.userId());
    Set<Long> allWithoutOwner =
        soptampUserQueryPort.findAllOfCurrentGeneration().stream()
            .map(SoptampUser::userId)
            .filter(userId -> !Objects.equals(userId, owner.userId()))
            .collect(Collectors.toSet());

    pushSenderPort.send(
        showcaseMessage(
            allWithoutOwner,
            stamp,
            false,
            nickname,
            ownerPart,
            mission,
            notificationTitle,
            notificationContent));
    pushSenderPort.send(
        showcaseMessage(
            Set.of(owner.userId()),
            stamp,
            true,
            nickname,
            ownerPart,
            mission,
            notificationTitle,
            notificationContent));
  }

  private PushMessage showcaseMessage(
      Set<Long> userIds,
      Stamp stamp,
      boolean isMine,
      String nickname,
      SoptampPart ownerPart,
      Mission mission,
      String title,
      String content) {
    String deepLink =
        SoptampDeepLinkBuilder.buildStampDetailLink(
            stamp.id(),
            isMine,
            nickname,
            ownerPart,
            mission.id(),
            mission.level(),
            mission.title());
    return new PushMessage(userIds, title, content, NotificationCategory.NOTICE, deepLink, null);
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
