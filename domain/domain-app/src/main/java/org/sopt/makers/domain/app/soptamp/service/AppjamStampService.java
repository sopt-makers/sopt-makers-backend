package org.sopt.makers.domain.app.soptamp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.appjam.port.AppjamUserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppjamStampService {

  private final AppjamUserRepositoryPort appjamUserRepositoryPort;
  private final StampService stampService;

  public void checkDuplicateStamp(TeamNumber teamNumber, Long missionId) {
    List<Long> teamUserIds =
        appjamUserRepositoryPort.findAllByTeamNumber(teamNumber).stream()
            .map(AppjamUser::userId)
            .toList();
    stampService.checkDuplicateStampByTeam(teamUserIds, missionId);
  }

  @Transactional
  public org.sopt.makers.domain.app.soptamp.stamp.Stamp register(
      Long userId, Long missionId, String contents, String image, String activityDate) {
    return stampService.register(userId, missionId, contents, image, activityDate);
  }
}
