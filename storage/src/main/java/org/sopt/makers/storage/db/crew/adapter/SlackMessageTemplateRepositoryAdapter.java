package org.sopt.makers.storage.db.crew.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.slack.SlackMessageTemplate;
import org.sopt.makers.domain.crew.slack.port.SlackMessageTemplateRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.SlackMessageTemplateEntity;
import org.sopt.makers.storage.db.crew.repository.SlackMessageTemplateJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackMessageTemplateRepositoryAdapter implements SlackMessageTemplateRepositoryPort {

  private final SlackMessageTemplateJpaRepository repository;

  @Override
  public Optional<SlackMessageTemplate> findByTemplateCode(String templateCode) {
    return repository.findById(templateCode).map(SlackMessageTemplateEntity::toDomain);
  }
}
