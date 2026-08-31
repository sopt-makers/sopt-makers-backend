package org.sopt.makers.storage.db.crew.repository;

import org.sopt.makers.storage.db.crew.entity.MeetingKeywordPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingKeywordPreferenceJpaRepository
    extends JpaRepository<MeetingKeywordPreferenceEntity, Long> {}
