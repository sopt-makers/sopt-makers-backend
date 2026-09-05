package org.sopt.makers.domain.app.soptamp.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTeamSummary;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUserStatus;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.appjam.port.AppjamUserRepositoryPort;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppjamUserService {

  private final AppjamUserRepositoryPort appjamUserRepositoryPort;

  public AppjamUserStatus getAppjamUserStatus(Long userId) {
    return appjamUserRepositoryPort
        .findByUserId(userId)
        .map(AppjamUserStatus::joined)
        .orElseGet(AppjamUserStatus::notJoined);
  }

  public AppjamTeamSummary getTeamSummaryByTeamNumber(TeamNumber teamNumber) {
    AppjamUser appjamUser =
        appjamUserRepositoryPort
            .findTopByTeamNumberOrderById(teamNumber)
            .orElseThrow(() -> new SoptampException(SoptampFailure.TEAM_NOT_FOUND));
    return AppjamTeamSummary.from(appjamUser);
  }

  public AppjamTeamSummary getTeamSummaryByUserId(Long userId) {
    return appjamUserRepositoryPort
        .findByUserId(userId)
        .map(AppjamTeamSummary::from)
        .orElseGet(AppjamTeamSummary::empty);
  }

  public boolean isAppjamParticipant(Long userId) {
    return appjamUserRepositoryPort.existsByUserId(userId);
  }
}
