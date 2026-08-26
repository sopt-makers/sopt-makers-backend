package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandCommentEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandCommentJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandCommentRepositoryAdapter implements MeetingDemandCommentRepositoryPort {

  private static final int PARENT_DEPTH = 0;
  private static final int REPLY_DEPTH = 1;

  private final MeetingDemandCommentJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemandComment save(MeetingDemandComment comment) {
    return repository.save(MeetingDemandCommentEntity.fromDomain(comment)).toDomain();
  }

  @Override
  @Transactional
  public List<MeetingDemandComment> saveAll(List<MeetingDemandComment> comments) {
    return repository
        .saveAll(comments.stream().map(MeetingDemandCommentEntity::fromDomain).toList())
        .stream()
        .map(MeetingDemandCommentEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<MeetingDemandComment> findById(Long commentId) {
    return repository.findById(commentId).map(MeetingDemandCommentEntity::toDomain);
  }

  @Override
  public Optional<MeetingDemandComment> findByIdForUpdate(Long commentId) {
    return repository.findByIdForUpdate(commentId).map(MeetingDemandCommentEntity::toDomain);
  }

  @Override
  public Page<MeetingDemandComment> findParentComments(Long meetingDemandId, Pageable pageable) {
    return repository
        .findAllByMeetingDemandIdAndDepth(meetingDemandId, PARENT_DEPTH, pageable)
        .map(MeetingDemandCommentEntity::toDomain);
  }

  @Override
  public List<MeetingDemandComment> findRepliesByParentIds(List<Long> parentIds) {
    if (parentIds == null || parentIds.isEmpty()) {
      return List.of();
    }
    return repository
        .findAllByParentIdInAndDepthOrderByParentIdAscOrderAsc(parentIds, REPLY_DEPTH)
        .stream()
        .map(MeetingDemandCommentEntity::toDomain)
        .toList();
  }

  @Override
  public List<MeetingDemandComment> findRepliesByParentId(Long parentId) {
    return repository.findAllByParentIdAndDepthOrderByOrderAsc(parentId, REPLY_DEPTH).stream()
        .map(MeetingDemandCommentEntity::toDomain)
        .toList();
  }

  @Override
  public int findMaxReplyOrder(Long parentId) {
    return repository
        .findFirstByParentIdOrderByOrderDesc(parentId)
        .map(MeetingDemandCommentEntity::getOrder)
        .orElse(0);
  }

  @Override
  public List<Long> findDistinctWriterUserIdsByMeetingDemandId(Long meetingDemandId) {
    return repository.findDistinctWriterUserIds(meetingDemandId);
  }

  @Override
  public List<Long> findAllIdsByMeetingDemandId(Long meetingDemandId) {
    return repository.findAllIdsByMeetingDemandId(meetingDemandId);
  }

  @Override
  @Transactional
  public void delete(MeetingDemandComment comment) {
    repository.delete(MeetingDemandCommentEntity.fromDomain(comment));
  }

  @Override
  @Transactional
  public void deleteAllByMeetingDemandId(Long meetingDemandId) {
    repository.deleteAllByMeetingDemandId(meetingDemandId);
  }
}
