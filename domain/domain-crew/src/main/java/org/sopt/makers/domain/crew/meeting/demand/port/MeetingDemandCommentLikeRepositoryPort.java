package org.sopt.makers.domain.crew.meeting.demand.port;

import java.util.List;
import java.util.Set;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentLike;

public interface MeetingDemandCommentLikeRepositoryPort {

  MeetingDemandCommentLike save(MeetingDemandCommentLike like);

  boolean existsByCommentIdAndUserId(Long commentId, Long userId);

  void deleteByCommentIdAndUserId(Long commentId, Long userId);

  void deleteAllByCommentId(Long commentId);

  void deleteAllByCommentIds(List<Long> commentIds);

  Set<Long> findLikedCommentIds(List<Long> commentIds, Long userId);
}
