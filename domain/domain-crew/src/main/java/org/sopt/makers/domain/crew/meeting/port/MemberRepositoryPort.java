package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.MemberRole;

public interface MemberRepositoryPort {

  Member save(Member member);

  List<Member> saveAll(List<Member> members);

  List<Member> findAllByMeetingId(Long meetingId);

  List<Member> findAllByMeetingIdsAndRole(List<Long> meetingIds, MemberRole role);

  long countByMeetingIdAndRole(Long meetingId, MemberRole role);

  void deleteByMeetingIdAndUserIdAndRole(Long meetingId, Long userId, MemberRole role);

  void deleteAllByMeetingIdAndRole(Long meetingId, MemberRole role);
}
