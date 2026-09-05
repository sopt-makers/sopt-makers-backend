package org.sopt.makers.storage.db.playground.member.ask.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.UserAsk;
import org.sopt.makers.domain.playground.member.ask.port.UserAskRepositoryPort;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAskEntity;
import org.sopt.makers.storage.db.playground.member.ask.repository.UserAskJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAskRepositoryAdapter implements UserAskRepositoryPort {

  private final UserAskJpaRepository userAskJpaRepository;

  @Transactional
  @Override
  public UserAsk save(UserAsk userAsk) {
    return userAskJpaRepository.save(UserAskEntity.fromDomain(userAsk)).toDomain();
  }

  @Override
  public Optional<UserAsk> findById(Long questionId) {
    return userAskJpaRepository.findById(questionId).map(UserAskEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long questionId) {
    userAskJpaRepository.deleteById(questionId);
  }
}
