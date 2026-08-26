package org.sopt.makers.storage.db.crew.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingDemandCommentJpaRepository
    extends JpaRepository<MeetingDemandCommentEntity, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT comment FROM MeetingDemandCommentEntity comment WHERE comment.id = :id")
  Optional<MeetingDemandCommentEntity> findByIdForUpdate(@Param("id") Long id);

  Page<MeetingDemandCommentEntity> findAllByMeetingDemandIdAndDepth(
      Long meetingDemandId, int depth, Pageable pageable);

  List<MeetingDemandCommentEntity> findAllByParentIdInAndDepthOrderByParentIdAscOrderAsc(
      List<Long> parentIds, int depth);

  List<MeetingDemandCommentEntity> findAllByParentIdAndDepthOrderByOrderAsc(
      Long parentId, int depth);

  Optional<MeetingDemandCommentEntity> findFirstByParentIdOrderByOrderDesc(Long parentId);

  @Query(
      """
      SELECT DISTINCT comment.userId
      FROM MeetingDemandCommentEntity comment
      WHERE comment.meetingDemandId = :meetingDemandId
        AND comment.userId IS NOT NULL
      """)
  List<Long> findDistinctWriterUserIds(@Param("meetingDemandId") Long meetingDemandId);

  @Query(
      "SELECT comment.id FROM MeetingDemandCommentEntity comment WHERE comment.meetingDemandId = :meetingDemandId")
  List<Long> findAllIdsByMeetingDemandId(@Param("meetingDemandId") Long meetingDemandId);

  void deleteAllByMeetingDemandId(Long meetingDemandId);
}
