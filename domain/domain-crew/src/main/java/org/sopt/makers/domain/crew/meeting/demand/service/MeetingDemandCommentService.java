package org.sopt.makers.domain.crew.meeting.demand.service;

import static org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReportTarget.COMMENT;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.ALREADY_REPORTED_MEETING_DEMAND_COMMENT;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.INVALID_MEETING_DEMAND_COMMENT_PARENT;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.NOT_FOUND_MEETING_DEMAND_COMMENT;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.NOT_FOUND_MEETING_DEMAND_USER;
import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.WRITER_CANNOT_REPORT_MEETING_DEMAND_COMMENT;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentLike;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentProfile;
import org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandException;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandBlockedUserPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentLikeRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentProfileRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandReportRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandCommentService {

  private static final String NOTIFICATION_CATEGORY = "NEWS";
  private static final String COMMENT_NOTIFICATION_TITLE = "내가 만든 모임 수요에 댓글이 달렸어요";
  private static final String COMMENT_NOTIFICATION_CONTENT = "새로운 댓글이 달렸어요.";

  private final MeetingDemandService meetingDemandService;
  private final MeetingDemandRepositoryPort meetingDemandRepositoryPort;
  private final MeetingDemandCommentRepositoryPort commentRepositoryPort;
  private final MeetingDemandCommentLikeRepositoryPort likeRepositoryPort;
  private final MeetingDemandCommentProfileRepositoryPort profileRepositoryPort;
  private final MeetingDemandReportRepositoryPort reportRepositoryPort;
  private final MeetingDemandBlockedUserPort blockedUserPort;
  private final MeetingUserPort meetingUserPort;
  private final MeetingDemandNotificationPublisher notificationPublisher;
  private final Clock clock;

  public PageResult<CommentView> findComments(
      Long meetingDemandId, Long userId, int page, int limit) {
    meetingDemandService.validateExists(meetingDemandId);
    PageResult<MeetingDemandComment> parentPage =
        findNormalizedParentPage(meetingDemandId, page, limit);
    List<MeetingDemandComment> parents = parentPage.content();
    List<MeetingDemandComment> replies =
        commentRepositoryPort.findRepliesByParentIds(
            parents.stream().map(MeetingDemandComment::id).toList());

    List<MeetingDemandComment> visibleComments = new ArrayList<>(parents);
    visibleComments.addAll(replies);
    Map<Long, MeetingDemandCommentProfile> profileMap =
        getProfileMap(meetingDemandId, visibleComments);
    Set<Long> likedCommentIds =
        likeRepositoryPort.findLikedCommentIds(
            visibleComments.stream().map(MeetingDemandComment::id).toList(), userId);
    Set<Long> blockedUserIds =
        blockedUserPort.findBlockedUserIds(
            userId,
            visibleComments.stream()
                .map(MeetingDemandComment::userId)
                .filter(candidate -> candidate != null)
                .distinct()
                .toList());

    Map<Long, List<ReplyView>> repliesByParent =
        replies.stream()
            .map(reply -> toReplyView(reply, userId, likedCommentIds, blockedUserIds, profileMap))
            .collect(
                Collectors.groupingBy(
                    view -> view.comment().parentId(), LinkedHashMap::new, Collectors.toList()));

    return parentPage.map(
        parent ->
            new CommentView(
                parent,
                profileMap.get(parent.userId()),
                likedCommentIds.contains(parent.id()),
                parent.isWriter(userId),
                parent.userId() != null && blockedUserIds.contains(parent.userId()),
                repliesByParent.getOrDefault(parent.id(), List.of())));
  }

  @Transactional
  public MeetingDemandComment createComment(
      Long meetingDemandId, CreateCommentCommand command, Long userId) {
    MeetingDemand demand = meetingDemandService.getDemandForUpdate(meetingDemandId);
    validateUser(userId);
    findOrCreateProfile(demand, userId);

    MeetingDemandComment comment;
    if (command.isParent()) {
      comment = MeetingDemandComment.createParent(meetingDemandId, userId, command.contents());
    } else {
      MeetingDemandComment parent = getCommentForUpdate(command.parentCommentId());
      if (!parent.meetingDemandId().equals(meetingDemandId) || !parent.isParent()) {
        throw new MeetingDemandException(INVALID_MEETING_DEMAND_COMMENT_PARENT);
      }
      comment =
          MeetingDemandComment.createReply(
              meetingDemandId,
              userId,
              command.contents(),
              parent.id(),
              commentRepositoryPort.findMaxReplyOrder(parent.id()) + 1);
    }

    MeetingDemandComment saved = commentRepositoryPort.save(comment);
    meetingDemandRepositoryPort.save(demand.increaseCommentCount());
    if (saved.isParent() && !demand.isWriter(userId)) {
      notificationPublisher.publish(
          new MeetingDemandNotification(
              List.of(demand.userId()),
              COMMENT_NOTIFICATION_TITLE,
              COMMENT_NOTIFICATION_CONTENT,
              NOTIFICATION_CATEGORY,
              "/suggest/detail?id=" + meetingDemandId));
    }
    return saved;
  }

  @Transactional
  public UpdatedComment updateComment(Long commentId, String contents, Long userId) {
    MeetingDemandComment comment = getCommentForUpdate(commentId);
    comment.validateWriter(userId);
    MeetingDemandComment saved = commentRepositoryPort.save(comment.updateContents(contents));
    return new UpdatedComment(saved, LocalDateTime.now(clock));
  }

  @Transactional
  public void deleteComment(Long commentId, Long userId) {
    MeetingDemandComment snapshot = getComment(commentId);
    MeetingDemand demand = meetingDemandService.getDemandForUpdate(snapshot.meetingDemandId());
    MeetingDemandComment comment = getCommentForUpdate(commentId);
    comment.validateWriter(userId);
    List<MeetingDemandComment> replies =
        comment.isParent() ? commentRepositoryPort.findRepliesByParentId(comment.id()) : List.of();

    if (comment.isReply() || replies.isEmpty()) {
      likeRepositoryPort.deleteAllByCommentId(commentId);
      reportRepositoryPort.deleteAllByTarget(COMMENT, commentId);
      commentRepositoryPort.delete(comment);
    } else {
      MeetingDemandCommentProfile profile = findOrCreateProfile(demand, userId);
      commentRepositoryPort.save(comment.deleteParent());
      commentRepositoryPort.saveAll(
          replies.stream()
              .map(
                  reply ->
                      reply.removeDeletedMention(profile.anonymousNickname(), profile.userId()))
              .toList());
    }
    meetingDemandRepositoryPort.save(demand.decreaseCommentCount());
  }

  @Transactional
  public boolean toggleCommentLike(Long commentId, Long userId) {
    validateUser(userId);
    MeetingDemandComment comment = getCommentForUpdate(commentId);
    boolean wasLiked = likeRepositoryPort.existsByCommentIdAndUserId(commentId, userId);
    if (wasLiked) {
      likeRepositoryPort.deleteByCommentIdAndUserId(commentId, userId);
      commentRepositoryPort.save(comment.decreaseLikeCount());
      return false;
    }
    likeRepositoryPort.save(MeetingDemandCommentLike.create(commentId, userId));
    commentRepositoryPort.save(comment.increaseLikeCount());
    return true;
  }

  @Transactional
  public MeetingDemandReport reportComment(Long commentId, Long userId) {
    MeetingDemandComment comment = getComment(commentId);
    if (comment.isWriter(userId)) {
      throw new MeetingDemandException(WRITER_CANNOT_REPORT_MEETING_DEMAND_COMMENT);
    }
    if (reportRepositoryPort.existsByUserIdAndTarget(userId, COMMENT, commentId)) {
      throw new MeetingDemandException(ALREADY_REPORTED_MEETING_DEMAND_COMMENT);
    }
    return reportRepositoryPort.save(MeetingDemandReport.comment(userId, commentId));
  }

  private PageResult<MeetingDemandComment> findNormalizedParentPage(
      Long meetingDemandId, int page, int limit) {
    PageResult<MeetingDemandComment> result =
        commentRepositoryPort.findParentComments(meetingDemandId, new PageQuery(page, limit));
    if (result.totalPages() > 0 && page > result.totalPages()) {
      return commentRepositoryPort.findParentComments(
          meetingDemandId, new PageQuery(result.totalPages(), limit));
    }
    return result;
  }

  private ReplyView toReplyView(
      MeetingDemandComment reply,
      Long userId,
      Set<Long> likedCommentIds,
      Set<Long> blockedUserIds,
      Map<Long, MeetingDemandCommentProfile> profileMap) {
    return new ReplyView(
        reply,
        profileMap.get(reply.userId()),
        likedCommentIds.contains(reply.id()),
        reply.isWriter(userId),
        reply.userId() != null && blockedUserIds.contains(reply.userId()));
  }

  private Map<Long, MeetingDemandCommentProfile> getProfileMap(
      Long meetingDemandId, List<MeetingDemandComment> comments) {
    List<Long> userIds =
        comments.stream()
            .map(MeetingDemandComment::userId)
            .filter(userId -> userId != null)
            .distinct()
            .toList();
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return profileRepositoryPort
        .findAllByMeetingDemandIdAndUserIds(meetingDemandId, userIds)
        .stream()
        .collect(Collectors.toMap(MeetingDemandCommentProfile::userId, Function.identity()));
  }

  private MeetingDemandCommentProfile findOrCreateProfile(MeetingDemand demand, Long userId) {
    return profileRepositoryPort
        .findByMeetingDemandIdAndUserId(demand.id(), userId)
        .orElseGet(
            () ->
                profileRepositoryPort.save(
                    MeetingDemandCommentProfile.create(
                        demand.id(),
                        userId,
                        demand.isWriter(userId) ? demand.anonymousNickname() : null,
                        demand.isWriter(userId) ? demand.anonymousImageNumber() : null)));
  }

  private void validateUser(Long userId) {
    if (meetingUserPort.findById(userId).isEmpty()) {
      throw new MeetingDemandException(NOT_FOUND_MEETING_DEMAND_USER);
    }
  }

  private MeetingDemandComment getComment(Long commentId) {
    return commentRepositoryPort
        .findById(commentId)
        .orElseThrow(() -> new MeetingDemandException(NOT_FOUND_MEETING_DEMAND_COMMENT));
  }

  private MeetingDemandComment getCommentForUpdate(Long commentId) {
    if (commentId == null) {
      throw new MeetingDemandException(INVALID_MEETING_DEMAND_COMMENT_PARENT);
    }
    return commentRepositoryPort
        .findByIdForUpdate(commentId)
        .orElseThrow(() -> new MeetingDemandException(NOT_FOUND_MEETING_DEMAND_COMMENT));
  }

  public record CreateCommentCommand(String contents, boolean isParent, Long parentCommentId) {}

  public record CommentView(
      MeetingDemandComment comment,
      MeetingDemandCommentProfile writer,
      boolean isLiked,
      boolean isMine,
      boolean isBlockedComment,
      List<ReplyView> replies) {}

  public record ReplyView(
      MeetingDemandComment comment,
      MeetingDemandCommentProfile writer,
      boolean isLiked,
      boolean isMine,
      boolean isBlockedComment) {}

  public record UpdatedComment(MeetingDemandComment comment, LocalDateTime updatedAt) {}
}
