package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingJpaRepository extends JpaRepository<MeetingEntity, Long> {

  List<MeetingEntity> findAllByUserId(Long userId);
}
