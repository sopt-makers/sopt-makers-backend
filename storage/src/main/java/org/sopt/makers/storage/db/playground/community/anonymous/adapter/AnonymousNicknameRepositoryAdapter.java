package org.sopt.makers.storage.db.playground.community.anonymous.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;
import org.sopt.makers.domain.playground.community.anonymous.port.AnonymousNicknameRepositoryPort;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousNicknameEntity;
import org.sopt.makers.storage.db.playground.community.anonymous.repository.AnonymousNicknameJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnonymousNicknameRepositoryAdapter implements AnonymousNicknameRepositoryPort {

  private final AnonymousNicknameJpaRepository anonymousNicknameJpaRepository;

  @Override
  public AnonymousNickname findRandomOne() {
    return anonymousNicknameJpaRepository.findRandomOne().toDomain();
  }

  @Override
  public AnonymousNickname findRandomOneExcludingIds(List<Long> excludeIds) {
    return anonymousNicknameJpaRepository.findRandomOneByIdNotIn(excludeIds).toDomain();
  }

  @Override
  public List<AnonymousNickname> findAllByNicknameIn(List<String> nicknames) {
    return anonymousNicknameJpaRepository.findAllByNicknameIn(nicknames).stream()
        .map(AnonymousNicknameEntity::toDomain)
        .toList();
  }
}
