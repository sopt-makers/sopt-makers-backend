package org.sopt.makers.storage.db.crew.repository;

import org.sopt.makers.storage.db.crew.entity.SlackMessageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackMessageTemplateJpaRepository
    extends JpaRepository<SlackMessageTemplateEntity, String> {}
