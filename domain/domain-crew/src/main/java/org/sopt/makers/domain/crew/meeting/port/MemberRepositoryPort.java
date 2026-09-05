package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.MemberRole;

public interface MemberRepositoryPort {

  Member save(Member member);

  Member saveOrReplaceRole(Member member);

  List<Member> findAllByMeetingId(Long meetingId);

  List<Member> findAllByUserId(Long userId);

  List<Member> findAllByMeetingIds(List<Long> meetingIds);

  Optional<Member> findByMeetingIdAndUserId(Long meetingId, Long userId);

  List<Member> findAllByMeetingIdsAndRole(List<Long> meetingIds, MemberRole role);

  List<Member> findAllByMeetingIdsAndUserId(List<Long> meetingIds, Long userId);

  long countByMeetingIdAndRole(Long meetingId, MemberRole role);

  void deleteByMeetingIdAndUserIdAndRole(Long meetingId, Long userId, MemberRole role);

  void deleteAllByMeetingId(Long meetingId);
}
