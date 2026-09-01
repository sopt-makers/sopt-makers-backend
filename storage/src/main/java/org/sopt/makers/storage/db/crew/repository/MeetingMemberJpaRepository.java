package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.storage.db.crew.entity.MeetingMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingMemberJpaRepository extends JpaRepository<MeetingMemberEntity, Long> {

  List<MeetingMemberEntity> findAllByMeetingId(Long meetingId);

  List<MeetingMemberEntity> findAllByMeetingIdInAndRole(List<Long> meetingIds, MemberRole role);

  long countByMeetingIdAndRole(Long meetingId, MemberRole role);

  void deleteByMeetingIdAndUserIdAndRole(Long meetingId, Long userId, MemberRole role);

  void deleteAllByMeetingIdAndRole(Long meetingId, MemberRole role);
}
