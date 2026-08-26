package org.sopt.makers.storage.db.app.soptletter.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterProfileRepositoryPort;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterProfileEntity;
import org.sopt.makers.storage.db.app.soptletter.repository.SoptLetterProfileJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptLetterProfileRepositoryAdapter implements SoptLetterProfileRepositoryPort {

  private final SoptLetterProfileJpaRepository soptLetterProfileJpaRepository;

  @Override
  public Optional<SoptLetterProfile> findById(Long profileId) {
    return soptLetterProfileJpaRepository
        .findById(profileId)
        .map(SoptLetterProfileEntity::toDomain);
  }

  @Override
  public Optional<SoptLetterProfile> findByUserId(Long userId) {
    return soptLetterProfileJpaRepository
        .findByUserId(userId)
        .map(SoptLetterProfileEntity::toDomain);
  }

  @Override
  public List<SoptLetterProfile> findAllByIds(Collection<Long> profileIds) {
    return soptLetterProfileJpaRepository.findAllById(profileIds).stream()
        .map(SoptLetterProfileEntity::toDomain)
        .toList();
  }

  @Override
  public boolean existsByUserId(Long userId) {
    return soptLetterProfileJpaRepository.existsByUserId(userId);
  }

  @Override
  public Set<String> findExistingNicknames(Collection<String> nicknames) {
    return Set.copyOf(soptLetterProfileJpaRepository.findExistingNicknames(nicknames));
  }

  @Override
  @Transactional
  public SoptLetterProfile save(SoptLetterProfile profile) {
    try {
      return soptLetterProfileJpaRepository
          .saveAndFlush(SoptLetterProfileEntity.from(profile))
          .toDomain();
    } catch (DataIntegrityViolationException e) {
      throw new SoptLetterException(SoptLetterFailure.DUPLICATE_SOPT_LETTER_PROFILE);
    }
  }

  @Override
  @Transactional
  public void completeOnboarding(Long profileId) {
    soptLetterProfileJpaRepository.completeOnboarding(profileId);
  }
}
