package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.MeetingTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingTagJpaRepository extends JpaRepository<MeetingTagEntity, Long> {

  Optional<MeetingTagEntity> findByMeetingId(Long meetingId);

  Optional<MeetingTagEntity> findByFlashId(Long flashId);

  List<MeetingTagEntity> findAllByMeetingIdIn(List<Long> meetingIds);

  void deleteByMeetingId(Long meetingId);

  void deleteByFlashId(Long flashId);
}
