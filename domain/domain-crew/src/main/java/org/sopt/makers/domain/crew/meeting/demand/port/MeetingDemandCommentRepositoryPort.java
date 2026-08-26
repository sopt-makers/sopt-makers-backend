package org.sopt.makers.domain.crew.meeting.demand.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingDemandCommentRepositoryPort {

  MeetingDemandComment save(MeetingDemandComment comment);

  List<MeetingDemandComment> saveAll(List<MeetingDemandComment> comments);

  Optional<MeetingDemandComment> findById(Long commentId);

  Optional<MeetingDemandComment> findByIdForUpdate(Long commentId);

  Page<MeetingDemandComment> findParentComments(Long meetingDemandId, Pageable pageable);

  List<MeetingDemandComment> findRepliesByParentIds(List<Long> parentIds);

  List<MeetingDemandComment> findRepliesByParentId(Long parentId);

  int findMaxReplyOrder(Long parentId);

  List<Long> findDistinctWriterUserIdsByMeetingDemandId(Long meetingDemandId);

  List<Long> findAllIdsByMeetingDemandId(Long meetingDemandId);

  void delete(MeetingDemandComment comment);

  void deleteAllByMeetingDemandId(Long meetingDemandId);
}
