package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandCommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDemandCommentLikeJpaRepository
    extends JpaRepository<MeetingDemandCommentLikeEntity, Long> {

  boolean existsByMeetingDemandCommentIdAndUserId(Long commentId, Long userId);

  void deleteByMeetingDemandCommentIdAndUserId(Long commentId, Long userId);

  void deleteAllByMeetingDemandCommentId(Long commentId);

  void deleteAllByMeetingDemandCommentIdIn(List<Long> commentIds);

  List<MeetingDemandCommentLikeEntity> findAllByMeetingDemandCommentIdInAndUserId(
      List<Long> commentIds, Long userId);
}
