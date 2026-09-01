package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.storage.db.crew.entity.MeetingMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingMemberJpaRepository extends JpaRepository<MeetingMemberEntity, Long> {

  List<MeetingMemberEntity> findAllByMeetingId(Long meetingId);

  Optional<MeetingMemberEntity> findByMeetingIdAndUserId(Long meetingId, Long userId);

  List<MeetingMemberEntity> findAllByMeetingIdInAndRole(List<Long> meetingIds, MemberRole role);

  List<MeetingMemberEntity> findAllByMeetingIdInAndUserId(List<Long> meetingIds, Long userId);

  long countByMeetingIdAndRole(Long meetingId, MemberRole role);

  void deleteByMeetingIdAndUserIdAndRole(Long meetingId, Long userId, MemberRole role);

  void deleteAllByMeetingId(Long meetingId);
}
