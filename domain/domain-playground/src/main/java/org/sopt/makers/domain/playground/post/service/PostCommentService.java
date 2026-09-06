package org.sopt.makers.domain.playground.post.service;

import static org.sopt.makers.domain.playground.post.exception.PostFailure.ALREADY_REPORTED_COMMENT;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.FORBIDDEN_MEETING_POST;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.INVALID_COMMENT_PARENT;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_COMMENT;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_MEETING;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_POST;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.NOT_FOUND_USER;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.MeetingPostContext;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostNotification;
import org.sopt.makers.domain.playground.post.PostWriter;
import org.sopt.makers.domain.playground.post.comment.PostComment;
import org.sopt.makers.domain.playground.post.exception.PostException;
import org.sopt.makers.domain.playground.post.like.PostCommentLike;
import org.sopt.makers.domain.playground.post.port.MeetingPostAccessPort;
import org.sopt.makers.domain.playground.post.port.PostCommentLikeRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostCommentReportRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostCommentRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostNotificationSenderPort;
import org.sopt.makers.domain.playground.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.post.report.PostCommentReport;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.PlaygroundPostUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

  private final PostRepositoryPort postRepositoryPort;
  private final PostCommentRepositoryPort commentRepositoryPort;
  private final PostCommentLikeRepositoryPort likeRepositoryPort;
  private final PostCommentReportRepositoryPort reportRepositoryPort;
  private final MeetingPostAccessPort meetingPostAccessPort;
  private final PlaygroundPostUserPort userPort;
  private final PostNotificationSenderPort notificationSenderPort;
  private final Clock clock;

  public PageResult<CommentView> findComments(Long postId, Long userId, int page, int limit) {
    Post post = getPost(postId);
    validateMeetingMember(post.meetingId(), userId);
    PageResult<PostComment> parentPage =
        commentRepositoryPort.findParents(postId, new PageQuery(page, limit));
    List<PostComment> replies =
        commentRepositoryPort.findReplies(
            parentPage.content().stream().map(PostComment::id).toList());
    List<PostComment> all =
        java.util.stream.Stream.concat(parentPage.content().stream(), replies.stream()).toList();

    Set<Long> likedIds =
        likeRepositoryPort.findLikedCommentIds(all.stream().map(PostComment::id).toList(), userId);
    Map<Long, PostWriter> writerMap =
        getWriterMap(
            all.stream()
                .map(PostComment::writerId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    Map<Long, List<CommentItem>> repliesByParent =
        replies.stream()
            .map(comment -> toItem(comment, userId, likedIds, writerMap))
            .collect(
                Collectors.groupingBy(
                    item -> item.comment().parentCommentId(),
                    java.util.LinkedHashMap::new,
                    Collectors.toList()));

    return parentPage.map(
        parent ->
            new CommentView(
                toItem(parent, userId, likedIds, writerMap),
                repliesByParent.getOrDefault(parent.id(), List.of())));
  }

  @Transactional
  public PostComment createComment(Long postId, CreateCommentCommand command, Long userId) {
    Post post = getPostForUpdate(postId);
    validateMeetingMember(post.meetingId(), userId);
    validateUser(userId);

    PostComment comment;
    if (command.parent()) {
      comment = PostComment.createParent(postId, userId, command.contents());
    } else {
      if (command.parentCommentId() == null) {
        throw new PostException(INVALID_COMMENT_PARENT);
      }
      PostComment parent = getComment(command.parentCommentId());
      if (!parent.isParent() || !parent.postId().equals(postId)) {
        throw new PostException(INVALID_COMMENT_PARENT);
      }
      comment =
          PostComment.createReply(
              postId,
              userId,
              command.contents(),
              parent.id(),
              commentRepositoryPort.findMaxReplyOrder(parent.id()) + 1);
    }

    PostComment saved = commentRepositoryPort.save(comment);
    postRepositoryPort.save(post.increaseCommentCount());
    sendNewCommentNotification(post, command.contents(), userId);
    return saved;
  }

  @Transactional
  public UpdatedComment updateComment(Long commentId, String contents, Long userId) {
    PostComment comment = getCommentForUpdate(commentId);
    Post post = getPost(comment.postId());
    validateMeetingMember(post.meetingId(), userId);
    comment.validateWriter(userId);
    PostComment saved = commentRepositoryPort.save(comment.update(contents));
    return new UpdatedComment(saved, LocalDateTime.now(clock));
  }

  @Transactional
  public void deleteComment(Long commentId, Long userId) {
    PostComment comment = getCommentForUpdate(commentId);
    Post post = getPost(comment.postId());
    validateMeetingMember(post.meetingId(), userId);
    comment.validateWriter(userId);
    commentRepositoryPort.save(comment.markDeleted());
  }

  @Transactional
  public boolean toggleCommentLike(Long commentId, Long userId) {
    PostComment comment = getCommentForUpdate(commentId);
    Post post = getPost(comment.postId());
    validateMeetingMember(post.meetingId(), userId);
    comment.validateNotDeleted();
    if (likeRepositoryPort.existsByCommentIdAndUserId(commentId, userId)) {
      likeRepositoryPort.deleteByCommentIdAndUserId(commentId, userId);
      commentRepositoryPort.save(comment.decreaseLikeCount());
      return false;
    }
    likeRepositoryPort.save(PostCommentLike.create(commentId, userId));
    commentRepositoryPort.save(comment.increaseLikeCount());
    return true;
  }

  @Transactional
  public PostCommentReport reportComment(Long commentId, Long userId) {
    PostComment comment = getComment(commentId);
    Post post = getPost(comment.postId());
    validateMeetingMember(post.meetingId(), userId);
    comment.validateNotDeleted();
    if (reportRepositoryPort.existsByCommentIdAndReporterId(commentId, userId)) {
      throw new PostException(ALREADY_REPORTED_COMMENT);
    }
    return reportRepositoryPort.save(PostCommentReport.create(commentId, userId));
  }

  public void mentionUsers(Long postId, List<Long> mentionedUserIds, String contents, Long userId) {
    Post post = getPost(postId);
    validateMeetingMember(post.meetingId(), userId);
    notificationSenderPort.send(
        new PostNotification(
            mentionedUserIds, "댓글에서 회원님이 언급됐어요.", contents, post.id(), post.meetingId()));
  }

  private void sendNewCommentNotification(Post post, String contents, Long userId) {
    if (post.writerId() == null || post.writerId().equals(userId)) {
      return;
    }
    notificationSenderPort.send(
        new PostNotification(
            List.of(post.writerId()),
            "내가 작성한 모임 글에 댓글이 달렸어요.",
            contents,
            post.id(),
            post.meetingId()));
  }

  private CommentItem toItem(
      PostComment comment, Long userId, Set<Long> likedIds, Map<Long, PostWriter> writerMap) {
    return new CommentItem(
        comment,
        writerMap.get(comment.writerId()),
        likedIds.contains(comment.id()),
        comment.isWriter(userId),
        false);
  }

  private Map<Long, PostWriter> getWriterMap(Collection<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return userPort.findAllWithActivitiesByIds(List.copyOf(userIds)).stream()
        .collect(Collectors.toMap(User::id, this::toWriter));
  }

  private PostWriter toWriter(User user) {
    Profile profile = user.profile();
    Activity latestActivity =
        user.activities() == null
            ? null
            : user.activities().activities().stream()
                .max(java.util.Comparator.comparingInt(Activity::generation))
                .orElse(null);
    return new PostWriter(
        user.id(),
        profile == null ? null : profile.name(),
        profile == null ? null : profile.profileImage(),
        latestActivity == null ? null : latestActivity.generation(),
        latestActivity == null || latestActivity.part() == null
            ? null
            : latestActivity.part().getName());
  }

  private void validateUser(Long userId) {
    if (userPort.findWithActivitiesById(userId).isEmpty()) {
      throw new PostException(NOT_FOUND_USER);
    }
  }

  private void validateMeetingMember(Long meetingId, Long userId) {
    MeetingPostContext context =
        meetingPostAccessPort
            .findMeeting(meetingId, userId)
            .orElseThrow(() -> new PostException(NOT_FOUND_MEETING));
    if (!context.member()) {
      throw new PostException(FORBIDDEN_MEETING_POST);
    }
  }

  private Post getPost(Long postId) {
    return postRepositoryPort.findById(postId).orElseThrow(() -> new PostException(NOT_FOUND_POST));
  }

  private Post getPostForUpdate(Long postId) {
    return postRepositoryPort
        .findByIdForUpdate(postId)
        .orElseThrow(() -> new PostException(NOT_FOUND_POST));
  }

  private PostComment getComment(Long commentId) {
    return commentRepositoryPort
        .findById(commentId)
        .orElseThrow(() -> new PostException(NOT_FOUND_COMMENT));
  }

  private PostComment getCommentForUpdate(Long commentId) {
    return commentRepositoryPort
        .findByIdForUpdate(commentId)
        .orElseThrow(() -> new PostException(NOT_FOUND_COMMENT));
  }

  public record CreateCommentCommand(String contents, boolean parent, Long parentCommentId) {}

  public record CommentItem(
      PostComment comment,
      PostWriter writer,
      boolean liked,
      boolean writerOwned,
      boolean blocked) {}

  public record CommentView(CommentItem parent, List<CommentItem> replies) {}

  public record UpdatedComment(PostComment comment, LocalDateTime updatedAt) {}
}
