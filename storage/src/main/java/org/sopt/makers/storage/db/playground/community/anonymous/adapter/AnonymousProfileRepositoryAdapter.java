package org.sopt.makers.storage.db.playground.community.anonymous.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.port.AnonymousProfileRepositoryPort;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousNicknameEntity;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousProfileEntity;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousProfileImageEntity;
import org.sopt.makers.storage.db.playground.community.anonymous.repository.AnonymousNicknameJpaRepository;
import org.sopt.makers.storage.db.playground.community.anonymous.repository.AnonymousProfileImageJpaRepository;
import org.sopt.makers.storage.db.playground.community.anonymous.repository.AnonymousProfileJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnonymousProfileRepositoryAdapter implements AnonymousProfileRepositoryPort {

  private final AnonymousProfileJpaRepository anonymousProfileJpaRepository;
  private final AnonymousNicknameJpaRepository anonymousNicknameJpaRepository;
  private final AnonymousProfileImageJpaRepository anonymousProfileImageJpaRepository;

  @Transactional
  @Override
  public AnonymousProfile save(AnonymousProfile profile) {
    AnonymousNicknameEntity nickname =
        anonymousNicknameJpaRepository.getReferenceById(profile.nickname().id());
    AnonymousProfileImageEntity profileImage =
        anonymousProfileImageJpaRepository.getReferenceById(profile.profileImage().id());

    AnonymousProfileEntity entity =
        AnonymousProfileEntity.of(profile.userId(), profile.postId(), nickname, profileImage);

    return anonymousProfileJpaRepository.save(entity).toDomain();
  }

  @Override
  public Optional<AnonymousProfile> findByUserIdAndPostId(Long userId, Long postId) {
    return anonymousProfileJpaRepository
        .findByUserIdAndPostId(userId, postId)
        .map(AnonymousProfileEntity::toDomain);
  }

  @Override
  public List<AnonymousProfile> findAllByPostId(Long postId) {
    return anonymousProfileJpaRepository.findAllByPostId(postId).stream()
        .map(AnonymousProfileEntity::toDomain)
        .toList();
  }

  @Override
  public List<AnonymousProfile> findRecentOrderByCreatedAtDesc(int limit) {
    return anonymousProfileJpaRepository.findRecentOrderByCreatedAtDesc(limit).stream()
        .map(AnonymousProfileEntity::toDomain)
        .toList();
  }

  @Override
  public List<String> findNicknamesByPostIdAndNicknamesIn(Long postId, List<String> nicknames) {
    return anonymousProfileJpaRepository.findNicknamesByPostIdAndNicknamesIn(postId, nicknames);
  }

  @Override
  public List<AnonymousProfile> findByPostIdAndNicknamesIn(Long postId, List<String> nicknames) {
    return anonymousProfileJpaRepository.findByPostIdAndNicknamesIn(postId, nicknames).stream()
        .map(AnonymousProfileEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<AnonymousProfile> findById(Long id) {
    return anonymousProfileJpaRepository.findById(id).map(AnonymousProfileEntity::toDomain);
  }

  @Override
  public List<AnonymousProfile> findAllByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return anonymousProfileJpaRepository.findAllByIdIn(ids).stream()
        .map(AnonymousProfileEntity::toDomain)
        .toList();
  }
}
