package org.sopt.makers.domain.app.soptamp.clap.service;

import java.util.Comparator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.clap.ClapEvent;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapMilestonePort;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.port.MissionRepositoryPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampProfileSourcePort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ClapEventListener {

  private static final String CACHE_SYNC_EXECUTOR = "cacheSyncTaskExecutor";

  private static final int FIRST_MILESTONE = 1;
  private static final int[] HUNDRED_MILESTONES = {100, 500};
  private static final int KILO_MILESTONE_STEP = 1000;
  private static final int KILO_MILESTONE_MAX = 10000;

  private static final String DEEP_LINK_BASE =
      "soptamp/entire-part-ranking/part-ranking/missions/missionDetail";

  private final ClapMilestonePort clapMilestonePort;
  private final StampRepositoryPort stampRepositoryPort;
  private final MissionRepositoryPort missionRepositoryPort;
  private final SoptampUserQueryPort soptampUserQueryPort;
  private final SoptampProfileSourcePort soptampProfileSourcePort;
  private final PushSenderPort pushSenderPort;

  @Async(CACHE_SYNC_EXECUTOR)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(final ClapEvent event) {
    AlarmSource source = null;

    if (markIfCrossed(event, FIRST_MILESTONE)) {
      source = loadAlarmSource(event);
      pushSenderPort.send(ofOwnerClapFirst(event, source));
    }

    for (int milestone : HUNDRED_MILESTONES) {
      if (markIfCrossed(event, milestone)) {
        source = source == null ? loadAlarmSource(event) : source;
        pushSenderPort.send(ofOwnerClap100Or500(event, source, milestone));
        break;
      }
    }

    // 한 번에 여러 구간(2000, 3000)을 넘어도 낮은 것만 처리
    // 정책상 상한 10000까지 발송 (필요 시 조정)
    for (int milestone = KILO_MILESTONE_STEP;
        milestone <= KILO_MILESTONE_MAX;
        milestone += KILO_MILESTONE_STEP) {
      if (markIfCrossed(event, milestone)) {
        source = source == null ? loadAlarmSource(event) : source;
        pushSenderPort.send(ofOwnerClapKilo(event, source, milestone));
        break;
      }
    }
  }

  private boolean markIfCrossed(ClapEvent event, int milestone) {
    return crossed(event.oldClapTotal(), event.newClapTotal(), milestone)
        && clapMilestonePort.tryMarkFirstHit(event.stampId(), milestone);
  }

  private boolean crossed(int oldTotal, int newTotal, int threshold) {
    return oldTotal < threshold && newTotal >= threshold;
  }

  private AlarmSource loadAlarmSource(ClapEvent event) {
    User owner =
        soptampProfileSourcePort
            .findByUserId(event.ownerUserId())
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
    SoptampPart ownerPart = latestPart(owner);
    String nickname =
        soptampUserQueryPort
            .findByUserId(event.ownerUserId())
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER))
            .nickname();

    Stamp stamp =
        stampRepositoryPort
            .findById(event.stampId())
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_STAMP));
    Mission mission =
        missionRepositoryPort
            .findById(stamp.missionId())
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_MISSION));

    return new AlarmSource(
        owner.profile().name(),
        ownerPart.getPartName(),
        ownerPart.getShortName(),
        nickname,
        stamp.missionId(),
        mission.title(),
        mission.level());
  }

  private SoptampPart latestPart(User owner) {
    Activity latest =
        owner.activities().activities().stream()
            .max(
                Comparator.comparingInt(Activity::generation)
                    .thenComparing(Activity::isSopt, Boolean::compare))
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_USER_PART));
    return SoptampPart.of(latest.part(), latest.role(), latest.team());
  }

  private PushMessage ofOwnerClapFirst(ClapEvent event, AlarmSource source) {
    return new PushMessage(
        Set.of(event.ownerUserId()),
        String.format("첫 박수 도착! 💌 ‘%s’ 에 누군가가 박수를 쳤어요 👀", source.missionTitle()),
        """
        내 미션 사진에 누군가 첫 박수를 남겼어요. 짝짝짝짝! 👏

        어떤 솝트인이 박수쳤는 지 확인할 수 있어요!

        서로에게 응원의 박수를 보내며 소통해 보세요!
        """,
        NotificationCategory.NEWS,
        buildStampDetailLink(event, source),
        null);
  }

  private PushMessage ofOwnerClap100Or500(ClapEvent event, AlarmSource source, int milestone) {
    return new PushMessage(
        Set.of(event.ownerUserId()),
        String.format("축하해요! %d번째 박수를 받았어요 🎉", milestone),
        String.format(
            """
            %s파트 %s님의 ‘%s’ 미션 사진이 %d번째 박수를 받았습니다. 짝짝짝짝! 👏

            정말 대단해요! 앞으로도 계속해서 멋진 미션을 인증하고 파트/개인 랭킹을 올려보세요.

            어떤 솝트인이 박수쳤는 지 확인할 수 있어요!

            서로에게 응원의 박수를 보내며 소통해 보세요!
            """,
            source.ownerPartName(), source.ownerName(), source.missionTitle(), milestone),
        NotificationCategory.NEWS,
        buildStampDetailLink(event, source),
        null);
  }

  private PushMessage ofOwnerClapKilo(ClapEvent event, AlarmSource source, int milestone) {
    return new PushMessage(
        Set.of(event.ownerUserId()),
        String.format("박수 누적 %d개 🎉 ‘%s’에 박수 갈채를 받고 있어요.", milestone, source.missionTitle()),
        String.format(
            """
            미션 ‘%s’ 사진이 %d번째 박수를 받았습니다. 짝짝짝짝! 👏

            정말 대단해요! 앞으로도 계속해서 멋진 미션을 인증하고 파트/개인 랭킹을 올려보세요.

            어떤 솝트인이 박수쳤는 지 확인할 수 있어요!

            서로에게 응원의 박수를 보내며 소통해 보세요!
            """,
            source.missionTitle(), milestone),
        NotificationCategory.NEWS,
        buildStampDetailLink(event, source),
        null);
  }

  private String buildStampDetailLink(ClapEvent event, AlarmSource source) {
    return String.format(
        "%s?id=%d&isMine=%s&nickname=%s&part=%s&missionId=%d&level=%d&missionTitle=%s",
        DEEP_LINK_BASE,
        event.stampId(),
        "true",
        source.ownerNickname(),
        source.ownerPartShortName(),
        source.missionId(),
        source.missionLevel(),
        source.missionTitle());
  }

  private record AlarmSource(
      String ownerName,
      String ownerPartName,
      String ownerPartShortName,
      String ownerNickname,
      long missionId,
      String missionTitle,
      int missionLevel) {}
}
