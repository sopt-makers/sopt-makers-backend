package org.sopt.makers.domain.playground.community.comment.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.ALREADY_DELETED_COMMENT;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.ALREADY_LIKED_COMMENT;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.COMMENT_NOT_BELONGS_TO_POST;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_PARENT_COMMENT_ID;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.MISSING_PARENT_COMMENT_ID;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_ANONYMOUS_NICKNAME_IN_POST;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_COMMENT;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_COMMUNITY_POST;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_WRITER;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_LIKED_COMMENT;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.UNAUTHORIZED_COMMENT_ACCESS;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousNicknameRetriever;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileRetriever;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileService;
import org.sopt.makers.domain.playground.community.comment.Comment;
import org.sopt.makers.domain.playground.community.comment.CommentLike;
import org.sopt.makers.domain.playground.community.comment.DeletedComment;
import org.sopt.makers.domain.playground.community.comment.ReportComment;
import org.sopt.makers.domain.playground.community.comment.port.CommentLikeRepositoryPort;
import org.sopt.makers.domain.playground.community.comment.port.CommentRepositoryPort;
import org.sopt.makers.domain.playground.community.comment.port.DeletedCommentRepositoryPort;
import org.sopt.makers.domain.playground.community.comment.port.ReportCommentRepositoryPort;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;
import org.sopt.makers.domain.playground.community.member.service.CommunityMemberAssembler;
import org.sopt.makers.domain.playground.community.notification.service.CommunityNotificationPublisher;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 커뮤니티 댓글 생성/수정/삭제, 좋아요/좋아요 취소, 신고 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentCommandService {

  private final CommentRepositoryPort commentRepositoryPort;
  private final CommentLikeRepositoryPort commentLikeRepositoryPort;
  private final ReportCommentRepositoryPort reportCommentRepositoryPort;
  private final DeletedCommentRepositoryPort deletedCommentRepositoryPort;
  private final PostRepositoryPort postRepositoryPort;
  private final CommunityMemberAssembler communityMemberAssembler;
  private final AnonymousProfileService anonymousProfileService;
  private final AnonymousProfileRetriever anonymousProfileRetriever;
  private final AnonymousNicknameRetriever anonymousNicknameRetriever;
  private final CommentMentionAnonymizer commentMentionAnonymizer;
  private final CommunityNotificationPublisher communityNotificationPublisher;

  public record CreateCommentCommand(
      String content,
      Boolean isBlindWriter,
      Boolean isChildComment,
      Long parentCommentId,
      String[] anonymousMentionNicknames,
      String webLink,
      Long[] mentionUserIds) {}

  @Transactional
  public Comment createComment(Long writerId, Long postId, CreateCommentCommand command) {
    CommunityMemberSummary writer = getWriterOrThrow(writerId);
    Post post = getPostOrThrow(postId);
    validateChildCommentConsistency(command);

    Comment parentComment = null;
    if (Boolean.TRUE.equals(command.isChildComment())) {
      parentComment = getCommentOrThrow(command.parentCommentId());
      validateAnonymousMentionNicknames(postId, command.anonymousMentionNicknames());
    }

    Comment created =
        commentRepositoryPort.save(
            Comment.create(postId, writerId, command.parentCommentId(), command.content(), command.isBlindWriter()));

    if (Boolean.TRUE.equals(command.isBlindWriter())) {
      AnonymousProfile profile = anonymousProfileService.getOrCreateAnonymousProfile(writerId, postId);
      created = commentRepositoryPort.save(created.withAnonymousProfileId(profile.id()));
    }

    publishCommentNotifications(writerId, writer.name(), post, parentComment, command);

    return created;
  }

  @Transactional
  public void updateComment(Long writerId, Long commentId, String content) {
    Comment comment = getCommentOrThrow(commentId);
    validateOwner(comment, writerId);
    validateNotDeleted(comment);

    commentRepositoryPort.save(comment.withContent(content));
  }

  @Transactional
  public void deleteComment(Long writerId, Long commentId) {
    Comment comment = getCommentOrThrow(commentId);
    validateOwner(comment, writerId);

    commentMentionAnonymizer.anonymizeMentionsInReplies(comment);
    deletedCommentRepositoryPort.save(DeletedComment.from(comment));
    commentRepositoryPort.save(comment.markAsDeleted());
  }

  @Transactional
  public void reportComment(Long reporterId, Long commentId) {
    Comment comment = getCommentOrThrow(commentId);

    CommunityMemberSummary reporter = communityMemberAssembler.getMemberSummary(reporterId);
    if (reporter != null) {
      communityNotificationPublisher.publishCommentReport(comment.postId(), reporter.name(), comment.content());
    }

    reportCommentRepositoryPort.save(ReportComment.create(commentId, reporterId));
  }

  @Transactional
  public void likeComment(Long userId, Long postId, Long commentId) {
    validateWriterExists(userId);
    Comment comment = validateCommentBelongsToPost(postId, commentId);

    if (commentLikeRepositoryPort.existsByUserIdAndCommentId(userId, commentId)) {
      throw new CommunityException(ALREADY_LIKED_COMMENT);
    }

    commentLikeRepositoryPort.save(CommentLike.create(userId, comment.id()));
  }

  @Transactional
  public void unlikeComment(Long userId, Long postId, Long commentId) {
    validateCommentBelongsToPost(postId, commentId);

    CommentLike like =
        commentLikeRepositoryPort
            .findByUserIdAndCommentId(userId, commentId)
            .orElseThrow(() -> new CommunityException(NOT_LIKED_COMMENT));

    commentLikeRepositoryPort.delete(like);
  }

  /** 게시글 삭제 시 호출되는 댓글 연계 삭제 처리: 댓글을 스냅샷으로 보관한 뒤 좋아요/신고/댓글 행을 모두 제거한다. */
  @Transactional
  public void deleteCommentsByPostId(Long postId) {
    List<Comment> comments = commentRepositoryPort.findAllByPostId(postId);

    if (comments.isEmpty()) {
      return;
    }

    List<Long> commentIds = comments.stream().map(Comment::id).toList();

    deletedCommentRepositoryPort.saveAll(comments.stream().map(DeletedComment::from).toList());
    commentLikeRepositoryPort.deleteAllByCommentIds(commentIds);
    reportCommentRepositoryPort.deleteAllByCommentIds(commentIds);
    commentRepositoryPort.deleteAllByPostId(postId);
  }

  private Comment getCommentOrThrow(Long commentId) {
    return commentRepositoryPort.findById(commentId).orElseThrow(() -> new CommunityException(NOT_FOUND_COMMENT));
  }

  private Post getPostOrThrow(Long postId) {
    return postRepositoryPort.findById(postId).orElseThrow(() -> new CommunityException(NOT_FOUND_COMMUNITY_POST));
  }

  private CommunityMemberSummary getWriterOrThrow(Long writerId) {
    CommunityMemberSummary summary = communityMemberAssembler.getMemberSummary(writerId);
    if (summary == null) {
      throw new CommunityException(NOT_FOUND_WRITER);
    }
    return summary;
  }

  private void validateChildCommentConsistency(CreateCommentCommand command) {
    if (Boolean.TRUE.equals(command.isChildComment()) && command.parentCommentId() == null) {
      throw new CommunityException(MISSING_PARENT_COMMENT_ID);
    }

    if (!Boolean.TRUE.equals(command.isChildComment()) && command.parentCommentId() != null) {
      throw new CommunityException(INVALID_PARENT_COMMENT_ID);
    }
  }

  private void validateWriterExists(Long writerId) {
    if (communityMemberAssembler.getMemberSummary(writerId) == null) {
      throw new CommunityException(NOT_FOUND_WRITER);
    }
  }

  /** 댓글 작성자를 제외한 게시글 작성자/부모 댓글 작성자/멘션 대상에게 푸시 알림을 발행한다. */
  private void publishCommentNotifications(
      Long writerId, String writerName, Post post, Comment parentComment, CreateCommentCommand command) {
    Long postAuthorId = post.writerId();

    if (!Objects.equals(postAuthorId, writerId)) {
      communityNotificationPublisher.publishCommentCreated(
          postAuthorId, writerName, command.content(), command.isBlindWriter(), command.webLink());
    }

    if (parentComment != null) {
      Long parentCommentAuthorId = parentComment.writerId();
      if (!Objects.equals(parentCommentAuthorId, writerId) && !Objects.equals(parentCommentAuthorId, postAuthorId)) {
        communityNotificationPublisher.publishReplyCreated(
            parentCommentAuthorId, writerName, command.content(), command.isBlindWriter(), command.webLink());
      }
    }

    if (command.mentionUserIds() != null && command.mentionUserIds().length > 0) {
      List<Long> mentionedUserIds =
          Arrays.stream(command.mentionUserIds()).filter(id -> !Objects.equals(id, writerId)).toList();
      communityNotificationPublisher.publishMention(
          mentionedUserIds, writerName, command.content(), command.isBlindWriter(), command.webLink());
    }
  }

  private void validateOwner(Comment comment, Long writerId) {
    if (!Objects.equals(comment.writerId(), writerId)) {
      throw new CommunityException(UNAUTHORIZED_COMMENT_ACCESS);
    }
  }

  private void validateNotDeleted(Comment comment) {
    if (Boolean.TRUE.equals(comment.isDeleted())) {
      throw new CommunityException(ALREADY_DELETED_COMMENT);
    }
  }

  private Comment validateCommentBelongsToPost(Long postId, Long commentId) {
    Comment comment = getCommentOrThrow(commentId);

    if (!Objects.equals(comment.postId(), postId)) {
      throw new CommunityException(COMMENT_NOT_BELONGS_TO_POST);
    }

    return comment;
  }

  private void validateAnonymousMentionNicknames(Long postId, String[] nicknames) {
    if (nicknames == null || nicknames.length == 0) {
      return;
    }

    anonymousNicknameRetriever.validateAnonymousNicknames(nicknames);

    List<String> foundNicknames =
        anonymousProfileRetriever.findNicknamesByPostIdAndNicknamesIn(postId, Arrays.asList(nicknames));
    Set<String> foundNicknameSet = Set.copyOf(foundNicknames);

    for (String nickname : nicknames) {
      if (!foundNicknameSet.contains(nickname)) {
        throw new CommunityException(NOT_FOUND_ANONYMOUS_NICKNAME_IN_POST);
      }
    }
  }
}
