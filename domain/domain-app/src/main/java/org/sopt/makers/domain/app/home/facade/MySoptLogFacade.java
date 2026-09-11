package org.sopt.makers.domain.app.home.facade;

import org.sopt.makers.domain.app.home.ActivityStatus;
import org.sopt.makers.domain.app.home.MySoptLog;
import org.sopt.makers.domain.app.poke.Friendship;
import org.sopt.makers.domain.app.poke.service.FriendService;
import org.sopt.makers.domain.app.poke.service.PokeService;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.clap.service.ClapService;
import org.sopt.makers.domain.app.soptamp.service.AppjamUserService;
import org.sopt.makers.domain.app.soptamp.service.StampService;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.AppHomeUserPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MySoptLogFacade {

  private final AppHomeUserPort appHomeUserPort;
  private final AppjamUserService appjamUserService;
  private final PokeService pokeService;
  private final FriendService friendService;
  private final StampService stampService;
  private final ClapService clapService;
  private final SoptampMode soptampMode;
  private final Long currentGeneration;

  public MySoptLogFacade(
      AppHomeUserPort appHomeUserPort,
      AppjamUserService appjamUserService,
      PokeService pokeService,
      FriendService friendService,
      StampService stampService,
      ClapService clapService,
      SoptampMode soptampMode,
      @Value("${sopt.current.generation}") Long currentGeneration) {
    this.appHomeUserPort = appHomeUserPort;
    this.appjamUserService = appjamUserService;
    this.pokeService = pokeService;
    this.friendService = friendService;
    this.stampService = stampService;
    this.clapService = clapService;
    this.soptampMode = soptampMode;
    this.currentGeneration = currentGeneration;
  }

  public MySoptLog getMySoptLog(Long userId) {
    User user = appHomeUserPort.getWithActivities(userId);
    boolean isAppjamMode = soptampMode.isAppjam();
    boolean isActive = ActivityStatus.of(user, currentGeneration) == ActivityStatus.ACTIVE;
    boolean isAppjamParticipant = appjamUserService.isAppjamParticipant(userId);

    int totalPokeCount = pokeService.getUserPokeCount(userId).intValue();
    int newFriendsPokeCount = sumPokeCount(userId, Friendship.NEW_FRIEND);
    int bestFriendsPokeCount = sumPokeCount(userId, Friendship.BEST_FRIEND);
    int soulmatesPokeCount = sumPokeCount(userId, Friendship.SOULMATE);

    if (!isActive && !isAppjamParticipant) {
      return MySoptLog.ofInactiveNonAppjam(
          isAppjamMode,
          totalPokeCount,
          newFriendsPokeCount,
          bestFriendsPokeCount,
          soulmatesPokeCount);
    }

    int soptampCount = stampService.getCompletedMissionCount(userId);
    int viewCount = stampService.getTotalViewCount(userId);
    int myClapCount = stampService.getTotalReceivedClapCount(userId);
    int clapCount = clapService.getTotalGivenClapCount(userId);

    if (isActive) {
      return MySoptLog.ofActive(
          isAppjamMode,
          isAppjamParticipant,
          soptampCount,
          viewCount,
          myClapCount,
          clapCount,
          totalPokeCount,
          newFriendsPokeCount,
          bestFriendsPokeCount,
          soulmatesPokeCount);
    }
    return MySoptLog.ofInactiveAppjamParticipant(
        isAppjamMode,
        soptampCount,
        viewCount,
        myClapCount,
        clapCount,
        totalPokeCount,
        newFriendsPokeCount,
        bestFriendsPokeCount,
        soulmatesPokeCount);
  }

  private int sumPokeCount(Long userId, Friendship friendship) {
    return friendService.sumPokeCountByFriendship(
        userId, friendship.getLowerLimit(), friendship.getUpperLimit());
  }
}
