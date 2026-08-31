package org.sopt.makers.storage.db.app.soptletter.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterTopicRepositoryPort;
import org.sopt.makers.storage.db.app.soptletter.entity.SoptLetterTopicEntity;
import org.sopt.makers.storage.db.app.soptletter.repository.SoptLetterTopicJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptLetterTopicRepositoryAdapter implements SoptLetterTopicRepositoryPort {

  private final SoptLetterTopicJpaRepository soptLetterTopicJpaRepository;

  @Override
  public Optional<SoptLetterTopic> findById(Long topicId) {
    return soptLetterTopicJpaRepository.findById(topicId).map(SoptLetterTopicEntity::toDomain);
  }

  @Override
  public List<SoptLetterTopic> findAllLatestFirst() {
    return toDomains(soptLetterTopicJpaRepository.findAllByOrderByCreatedAtDesc());
  }

  @Override
  public List<SoptLetterTopic> findDefaultTopicsLatestFirst() {
    return toDomains(soptLetterTopicJpaRepository.findAllByIsDefaultOrderByCreatedAtDesc(true));
  }

  @Override
  public List<SoptLetterTopic> findNormalTopicsLatestFirst() {
    return toDomains(soptLetterTopicJpaRepository.findAllByIsDefaultOrderByCreatedAtDesc(false));
  }

  @Override
  public boolean existsNormalTopic() {
    return soptLetterTopicJpaRepository.existsByIsDefaultFalse();
  }

  @Override
  public List<SoptLetterTopic> findActiveCtasLatestFirst(LocalDateTime now) {
    return toDomains(soptLetterTopicJpaRepository.findActiveCtas(now));
  }

  private List<SoptLetterTopic> toDomains(List<SoptLetterTopicEntity> entities) {
    return entities.stream().map(SoptLetterTopicEntity::toDomain).toList();
  }
}
