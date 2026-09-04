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
import org.sopt.makers.domain.playground.community.member.service.CommunityMemberAssembler;
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

  public record CreateCommentCommand(
      String content,
      Boolean isBlindWriter,
      Boolean isChildComment,
      Long parentCommentId,
      String[] anonymousMentionNicknames) {}

  @Transactional
  public Comment createComment(Long writerId, Long postId, CreateCommentCommand command) {
    validateWriterExists(writerId);
    validatePostExists(postId);
    validateChildCommentConsistency(command);

    if (Boolean.TRUE.equals(command.isChildComment())) {
      validateParentCommentExists(command.parentCommentId());
      validateAnonymousMentionNicknames(postId, command.anonymousMentionNicknames());
    }

    Comment created =
        commentRepositoryPort.save(
            Comment.create(postId, writerId, command.parentCommentId(), command.content(), command.isBlindWriter()));

    if (Boolean.TRUE.equals(command.isBlindWriter())) {
      AnonymousProfile profile = anonymousProfileService.getOrCreateAnonymousProfile(writerId, postId);
      created = commentRepositoryPort.save(created.withAnonymousProfileId(profile.id()));
    }

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
    getCommentOrThrow(commentId);

    // TODO: 신고 Slack 알림 연동은 SlackClient 마이그레이션 이후 별도 처리 예정.
    reportCommentRepositoryPort.save(ReportComment.create(commentId, reporterId));
  }

  @Transactional
  public void likeComment(Long memberId, Long postId, Long commentId) {
    validateWriterExists(memberId);
    Comment comment = validateCommentBelongsToPost(postId, commentId);

    if (commentLikeRepositoryPort.existsByMemberIdAndCommentId(memberId, commentId)) {
      throw new CommunityException(ALREADY_LIKED_COMMENT);
    }

    commentLikeRepositoryPort.save(CommentLike.create(memberId, comment.id()));
  }

  @Transactional
  public void unlikeComment(Long memberId, Long postId, Long commentId) {
    validateCommentBelongsToPost(postId, commentId);

    CommentLike like =
        commentLikeRepositoryPort
            .findByMemberIdAndCommentId(memberId, commentId)
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

  private void validatePostExists(Long postId) {
    if (!postRepositoryPort.existsById(postId)) {
      throw new CommunityException(NOT_FOUND_COMMUNITY_POST);
    }
  }

  private void validateChildCommentConsistency(CreateCommentCommand command) {
    if (Boolean.TRUE.equals(command.isChildComment()) && command.parentCommentId() == null) {
      throw new CommunityException(MISSING_PARENT_COMMENT_ID);
    }

    if (!Boolean.TRUE.equals(command.isChildComment()) && command.parentCommentId() != null) {
      throw new CommunityException(INVALID_PARENT_COMMENT_ID);
    }
  }

  private void validateParentCommentExists(Long parentCommentId) {
    if (!commentRepositoryPort.existsById(parentCommentId)) {
      throw new CommunityException(NOT_FOUND_COMMENT);
    }
  }

  private void validateWriterExists(Long writerId) {
    if (communityMemberAssembler.getMemberSummary(writerId) == null) {
      throw new CommunityException(NOT_FOUND_WRITER);
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
