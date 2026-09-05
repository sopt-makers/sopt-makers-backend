package org.sopt.makers.domain.app.soptamp.clap.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.domain.app.soptamp.clap.ClapUserProfile;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapRepositoryPort;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampProfileSourcePort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.sopt.makers.domain.user.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClapService {

  private static final int MAX_LOCK_RETRY = 3;

  private static final int MAX_UNIQUE_RETRY = 3;

  private final ClapRegistrar clapRegistrar;
  private final ClapRepositoryPort clapRepositoryPort;
  private final StampRepositoryPort stampRepositoryPort;
  private final SoptampUserQueryPort soptampUserQueryPort;
  private final SoptampProfileSourcePort soptampProfileSourcePort;

  public int addClap(Long userId, Long stampId, int increment) {
    if (increment <= 0) {
      throw new SoptampException(SoptampFailure.INVALID_CLAP_COUNT);
    }

    int lockAttempt = 0;
    int uniqueAttempt = 0;
    while (true) {
      try {
        return clapRegistrar.register(userId, stampId, increment);
      } catch (OptimisticLockingFailureException e) {
        if (++lockAttempt >= MAX_LOCK_RETRY) {
          throw e;
        }
      } catch (DataIntegrityViolationException e) {
        if (++uniqueAttempt >= MAX_UNIQUE_RETRY) {
          throw e;
        }
      }
    }
  }

  @Transactional(readOnly = true)
  public Page<ClapUserProfile> getClapsOfMyStamp(Long userId, Long stampId, Pageable pageable) {
    checkOwnedStamp(stampId, userId);

    List<Clap> claps = clapRepositoryPort.findAllByStampIdOrderByClapCountDesc(stampId, pageable);
    List<Long> userIds = claps.stream().map(Clap::userId).distinct().toList();

    Map<Long, SoptampUser> profiles = soptampUserQueryPort.findByUserIdsAsMap(userIds);
    Map<Long, String> profileImages = findProfileImages(userIds);

    List<ClapUserProfile> users =
        claps.stream().map(clap -> toClapUserProfile(clap, profiles, profileImages)).toList();
    return PageableExecutionUtils.getPage(
        users, pageable, () -> clapRepositoryPort.countByStampId(stampId));
  }

  private void checkOwnedStamp(Long stampId, Long userId) {
    Stamp stamp =
        stampRepositoryPort
            .findById(stampId)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_STAMP));
    if (!Objects.equals(stamp.userId(), userId)) {
      throw new SoptampException(SoptampFailure.FORBIDDEN_CLAP_LIST);
    }
  }

  private Map<Long, String> findProfileImages(List<Long> userIds) {
    return soptampProfileSourcePort.findAllByUserIds(userIds).stream()
        .filter(user -> user.profile().profileImage() != null)
        .collect(Collectors.toMap(User::id, user -> user.profile().profileImage()));
  }

  private ClapUserProfile toClapUserProfile(
      Clap clap, Map<Long, SoptampUser> profiles, Map<Long, String> profileImages) {
    SoptampUser profile = profiles.get(clap.userId());
    if (profile == null) {
      throw new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER);
    }
    return new ClapUserProfile(
        profile.nickname(),
        profileImages.getOrDefault(clap.userId(), ""),
        profile.profileMessage(),
        clap.clapCount());
  }
}
