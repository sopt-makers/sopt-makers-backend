package org.sopt.makers.storage.db.app.soptletter.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterRepositoryPort;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterEntity;
import org.sopt.makers.storage.db.app.soptletter.repository.SoptLetterJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptLetterRepositoryAdapter implements SoptLetterRepositoryPort {

  private final SoptLetterJpaRepository soptLetterJpaRepository;

  @Override
  public Optional<SoptLetter> findById(Long letterId) {
    return soptLetterJpaRepository.findById(letterId).map(SoptLetterEntity::toDomain);
  }

  @Override
  public List<SoptLetter> findPageByTopicId(Long topicId, Long cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);
    List<SoptLetterEntity> entities =
        cursor == null
            ? soptLetterJpaRepository.findAllByTopicIdOrderByIdDesc(topicId, pageable)
            : soptLetterJpaRepository.findAllByTopicIdAndIdLessThanOrderByIdDesc(
                topicId, cursor, pageable);
    return entities.stream().map(SoptLetterEntity::toDomain).toList();
  }

  @Override
  public Optional<SoptLetter> findLatestByTopicId(Long topicId) {
    return soptLetterJpaRepository
        .findFirstByTopicIdOrderByIdDesc(topicId)
        .map(SoptLetterEntity::toDomain);
  }

  @Override
  public long countByTopicId(Long topicId) {
    return soptLetterJpaRepository.countByTopicId(topicId);
  }

  @Override
  public long countByAuthorProfileIdSince(Long authorProfileId, LocalDateTime since) {
    return soptLetterJpaRepository.countByAuthorProfileIdAndCreatedAtGreaterThanEqual(
        authorProfileId, since);
  }

  @Override
  public boolean existsByIdAndTopicId(Long letterId, Long topicId) {
    return soptLetterJpaRepository.existsByIdAndTopicId(letterId, topicId);
  }

  @Override
  @Transactional
  public SoptLetter save(SoptLetter soptLetter) {
    return soptLetterJpaRepository.save(SoptLetterEntity.from(soptLetter)).toDomain();
  }

  @Override
  @Transactional
  public void updateMessage(Long letterId, String message) {
    soptLetterJpaRepository.updateMessage(letterId, message);
  }

  @Override
  @Transactional
  public void deleteById(Long letterId) {
    soptLetterJpaRepository.deleteById(letterId);
  }

  @Override
  @Transactional
  public void increaseLikeCount(Long letterId) {
    soptLetterJpaRepository.increaseLikeCount(letterId);
  }

  @Override
  @Transactional
  public void decreaseLikeCount(Long letterId) {
    soptLetterJpaRepository.decreaseLikeCount(letterId);
  }
}
