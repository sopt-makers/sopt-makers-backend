package org.sopt.makers.storage.db.playground.community.anonymous.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;
import org.sopt.makers.domain.playground.community.anonymous.port.AnonymousProfileImageRepositoryPort;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousProfileImageEntity;
import org.sopt.makers.storage.db.playground.community.anonymous.repository.AnonymousProfileImageJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnonymousProfileImageRepositoryAdapter implements AnonymousProfileImageRepositoryPort {

  private final AnonymousProfileImageJpaRepository anonymousProfileImageJpaRepository;

  @Override
  public List<AnonymousProfileImage> findAllByIdNot(Long excludeId) {
    return anonymousProfileImageJpaRepository.findAllByIdNot(excludeId).stream()
        .map(AnonymousProfileImageEntity::toDomain)
        .toList();
  }
}
