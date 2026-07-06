package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.storage.db.crew.entity.CoLeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoLeaderJpaRepository extends JpaRepository<CoLeaderEntity, Long> {

  List<CoLeaderEntity> findAllByMeetingId(Long meetingId);

  List<CoLeaderEntity> findAllByUserId(Long userId);

  boolean existsByMeetingIdAndUserId(Long meetingId, Long userId);

  void deleteAllByMeetingId(Long meetingId);
}
