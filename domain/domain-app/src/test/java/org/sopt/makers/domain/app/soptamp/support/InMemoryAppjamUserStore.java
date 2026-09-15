package org.sopt.makers.domain.app.soptamp.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.appjam.port.AppjamUserRepositoryPort;

public final class InMemoryAppjamUserStore implements AppjamUserRepositoryPort {

  private final List<AppjamUser> store = new ArrayList<>();

  public void save(AppjamUser appjamUser) {
    store.add(appjamUser);
  }

  @Override
  public List<AppjamUser> findAll() {
    return List.copyOf(store);
  }

  @Override
  public List<AppjamUser> findAllByTeamNumber(TeamNumber teamNumber) {
    return store.stream().filter(it -> it.teamNumber() == teamNumber).toList();
  }

  @Override
  public Optional<AppjamUser> findTopByTeamNumberOrderById(TeamNumber teamNumber) {
    return findAllByTeamNumber(teamNumber).stream().findFirst();
  }

  @Override
  public Optional<AppjamUser> findByUserId(Long userId) {
    return store.stream().filter(it -> it.userId().equals(userId)).findFirst();
  }

  @Override
  public List<AppjamUser> findAllByTeamNumberIn(Collection<TeamNumber> teamNumbers) {
    return store.stream().filter(it -> teamNumbers.contains(it.teamNumber())).toList();
  }

  @Override
  public List<AppjamUser> findAllByUserIdIn(Collection<Long> userIds) {
    return store.stream().filter(it -> userIds.contains(it.userId())).toList();
  }

  @Override
  public boolean existsByUserId(Long userId) {
    return findByUserId(userId).isPresent();
  }
}
