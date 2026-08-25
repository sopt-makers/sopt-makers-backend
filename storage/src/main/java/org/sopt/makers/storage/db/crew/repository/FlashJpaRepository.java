package org.sopt.makers.storage.db.crew.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.FlashEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashJpaRepository extends JpaRepository<FlashEntity, Long> {

  Optional<FlashEntity> findByMeetingId(Long meetingId);

  void deleteByMeetingId(Long meetingId);
}
