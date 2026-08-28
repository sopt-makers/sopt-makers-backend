package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.slack.SlackEmojiMapping;
import org.sopt.makers.domain.crew.slack.port.SlackEmojiMappingRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.SlackEmojiMappingEntity;
import org.sopt.makers.storage.db.crew.repository.SlackEmojiMappingJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackEmojiMappingRepositoryAdapter implements SlackEmojiMappingRepositoryPort {

  private final SlackEmojiMappingJpaRepository repository;

  @Override
  public boolean existsByCallEmojiAndUserSlackId(String callEmoji, String userSlackId) {
    return repository.existsByCallEmojiAndUserSlackId(callEmoji, userSlackId);
  }

  @Override
  public List<SlackEmojiMapping> findAllByCallEmoji(String callEmoji) {
    return repository.findAllByCallEmojiOrderByIdAsc(callEmoji).stream()
        .map(SlackEmojiMappingEntity::toDomain)
        .toList();
  }

  @Override
  public List<SlackEmojiMapping> findAllByCallEmojiForUpdate(String callEmoji) {
    return repository.findAllByCallEmojiForUpdate(callEmoji).stream()
        .map(SlackEmojiMappingEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public SlackEmojiMapping save(SlackEmojiMapping mapping) {
    return repository.save(SlackEmojiMappingEntity.fromDomain(mapping)).toDomain();
  }

  @Override
  @Transactional
  public void updateCallEmoji(String originalCallEmoji, String updatedCallEmoji) {
    repository.updateCallEmoji(originalCallEmoji, updatedCallEmoji);
  }

  @Override
  @Transactional
  public void deleteAllByCallEmoji(String callEmoji) {
    repository.deleteAllByCallEmoji(callEmoji);
  }
}
