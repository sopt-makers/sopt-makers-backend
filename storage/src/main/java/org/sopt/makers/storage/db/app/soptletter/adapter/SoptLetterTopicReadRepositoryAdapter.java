package org.sopt.makers.storage.db.app.soptletter.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterTopicReadRepositoryPort;
import org.sopt.makers.storage.db.app.soptletter.repository.SoptLetterTopicReadJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SoptLetterTopicReadRepositoryAdapter implements SoptLetterTopicReadRepositoryPort {

  private final SoptLetterTopicReadJpaRepository soptLetterTopicReadJpaRepository;

  @Override
  @Transactional
  public void upsertLastReadLetterId(Long userId, Long topicId, Long lastReadLetterId) {
    soptLetterTopicReadJpaRepository.upsertLastReadLetterId(userId, topicId, lastReadLetterId);
  }
}
