package org.sopt.makers.storage.db.crew.repository;

import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingJpaRepository extends JpaRepository<MeetingEntity, Long> {

  Page<MeetingEntity> findAllByUserId(Long userId, Pageable pageable);
}
