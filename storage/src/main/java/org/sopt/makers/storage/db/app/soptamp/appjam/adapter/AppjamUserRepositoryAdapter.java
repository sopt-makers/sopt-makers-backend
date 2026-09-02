package org.sopt.makers.storage.db.app.soptamp.appjam.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.appjam.port.AppjamUserRepositoryPort;
import org.sopt.makers.storage.db.app.soptamp.appjam.entity.AppjamUserEntity;
import org.sopt.makers.storage.db.app.soptamp.appjam.repository.AppjamUserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppjamUserRepositoryAdapter implements AppjamUserRepositoryPort {

  private final AppjamUserJpaRepository appjamUserJpaRepository;

  @Override
  public List<AppjamUser> findAll() {
    return appjamUserJpaRepository.findAll().stream().map(AppjamUserEntity::toDomain).toList();
  }

  @Override
  public List<AppjamUser> findAllByTeamNumber(TeamNumber teamNumber) {
    return appjamUserJpaRepository.findAllByTeamNumber(teamNumber).stream()
        .map(AppjamUserEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<AppjamUser> findTopByTeamNumberOrderById(TeamNumber teamNumber) {
    return appjamUserJpaRepository
        .findTopByTeamNumberOrderById(teamNumber)
        .map(AppjamUserEntity::toDomain);
  }

  @Override
  public Optional<AppjamUser> findByUserId(Long userId) {
    return appjamUserJpaRepository.findByUserId(userId).map(AppjamUserEntity::toDomain);
  }

  @Override
  public List<AppjamUser> findAllByTeamNumberIn(Collection<TeamNumber> teamNumbers) {
    return appjamUserJpaRepository.findAllByTeamNumberIn(teamNumbers).stream()
        .map(AppjamUserEntity::toDomain)
        .toList();
  }

  @Override
  public List<AppjamUser> findAllByUserIdIn(Collection<Long> userIds) {
    return appjamUserJpaRepository.findAllByUserIdIn(userIds).stream()
        .map(AppjamUserEntity::toDomain)
        .toList();
  }

  @Override
  public boolean existsByUserId(Long userId) {
    return appjamUserJpaRepository.existsByUserId(userId);
  }
}
