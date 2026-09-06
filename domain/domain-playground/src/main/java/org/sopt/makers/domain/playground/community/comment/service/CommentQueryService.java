package org.sopt.makers.domain.playground.community.comment.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_COMMUNITY_POST;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileRetriever;
import org.sopt.makers.domain.playground.community.comment.Comment;
import org.sopt.makers.domain.playground.community.comment.CommentThread;
import org.sopt.makers.domain.playground.community.comment.port.CommentLikeRepositoryPort;
import org.sopt.makers.domain.playground.community.comment.port.CommentRepositoryPort;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;
import org.sopt.makers.domain.playground.community.member.service.CommunityMemberAssembler;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.member.relation.port.UserBlockRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 커뮤니티 댓글 조회 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {

  private final CommentRepositoryPort commentRepositoryPort;
  private final CommentLikeRepositoryPort commentLikeRepositoryPort;
  private final PostRepositoryPort postRepositoryPort;
  private final CommunityMemberAssembler communityMemberAssembler;
  private final AnonymousProfileRetriever anonymousProfileRetriever;
  private final UserBlockRepositoryPort userBlockRepositoryPort;

  public List<CommentThread> getCommentThreadsByPostId(Long viewerId, Long postId, Boolean isBlockOn) {
    if (!postRepositoryPort.existsById(postId)) {
      throw new CommunityException(NOT_FOUND_COMMUNITY_POST);
    }

    Set<Long> blockedWriterIds = resolveBlockedWriterIds(viewerId, isBlockOn);
    List<Comment> comments = excludeBlockedWriters(commentRepositoryPort.findAllByPostId(postId), blockedWriterIds);

    return toCommentThreads(comments, viewerId);
  }

  public Map<Long, List<CommentThread>> getCommentThreadsByPostIds(
      Long viewerId, List<Long> postIds, Set<Long> blockedWriterIds) {
    if (postIds == null || postIds.isEmpty()) {
      return Map.of();
    }

    List<Comment> comments = excludeBlockedWriters(commentRepositoryPort.findAllByPostIds(postIds), blockedWriterIds);
    List<CommentThread> threads = toCommentThreads(comments, viewerId);
    Map<Long, List<CommentThread>> grouped =
        threads.stream().collect(Collectors.groupingBy(thread -> thread.comment().postId()));

    return postIds.stream().collect(Collectors.toMap(postId -> postId, postId -> grouped.getOrDefault(postId, List.of())));
  }

  private Set<Long> resolveBlockedWriterIds(Long viewerId, Boolean isBlockOn) {
    if (viewerId == null || !Boolean.TRUE.equals(isBlockOn)) {
      return Set.of();
    }
    return userBlockRepositoryPort.findBlockedUserIdsInvolving(viewerId);
  }

  private List<Comment> excludeBlockedWriters(List<Comment> comments, Set<Long> blockedWriterIds) {
    if (blockedWriterIds == null || blockedWriterIds.isEmpty()) {
      return comments;
    }
    return comments.stream().filter(comment -> !blockedWriterIds.contains(comment.writerId())).toList();
  }

  public Map<Long, Integer> countNonDeletedCommentsByPostIds(List<Long> postIds) {
    if (postIds == null || postIds.isEmpty()) {
      return Map.of();
    }

    return toIntMap(commentRepositoryPort.countNonDeletedByPostIds(postIds));
  }

  private List<CommentThread> toCommentThreads(List<Comment> comments, Long viewerId) {
    if (comments.isEmpty()) {
      return List.of();
    }

    List<Long> writerIds = comments.stream().map(Comment::writerId).distinct().toList();
    List<Long> anonymousProfileIds =
        comments.stream().map(Comment::anonymousProfileId).filter(Objects::nonNull).distinct().toList();
    List<Long> commentIds = comments.stream().map(Comment::id).toList();

    Map<Long, CommunityMemberSummary> memberMap = communityMemberAssembler.getMemberSummaryMap(writerIds);
    Map<Long, AnonymousProfile> anonymousProfileMap = anonymousProfileRetriever.findAllByIdsAsMap(anonymousProfileIds);
    Map<Long, Boolean> likedMap = getLikedMap(viewerId, commentIds);
    Map<Long, Integer> likeCountMap = toIntMap(commentLikeRepositoryPort.countLikesByCommentIds(commentIds));

    return comments.stream()
        .map(comment -> toCommentThread(comment, viewerId, memberMap, anonymousProfileMap, likedMap, likeCountMap))
        .toList();
  }

  private CommentThread toCommentThread(
      Comment comment,
      Long viewerId,
      Map<Long, CommunityMemberSummary> memberMap,
      Map<Long, AnonymousProfile> anonymousProfileMap,
      Map<Long, Boolean> likedMap,
      Map<Long, Integer> likeCountMap) {
    boolean isBlind = Boolean.TRUE.equals(comment.isBlindWriter());
    CommunityMemberSummary member = isBlind ? null : memberMap.get(comment.writerId());
    AnonymousProfile anonymousProfile =
        isBlind && comment.anonymousProfileId() != null ? anonymousProfileMap.get(comment.anonymousProfileId()) : null;
    boolean isMine = Objects.equals(comment.writerId(), viewerId);

    return new CommentThread(
        comment,
        member,
        anonymousProfile,
        isMine,
        likedMap.getOrDefault(comment.id(), false),
        likeCountMap.getOrDefault(comment.id(), 0));
  }

  private Map<Long, Boolean> getLikedMap(Long viewerId, List<Long> commentIds) {
    if (commentIds.isEmpty()) {
      return Map.of();
    }

    List<Long> likedCommentIds = commentLikeRepositoryPort.findLikedCommentIdsByMemberIdAndCommentIds(viewerId, commentIds);
    Set<Long> likedCommentIdSet = Set.copyOf(likedCommentIds);

    return commentIds.stream().collect(Collectors.toMap(commentId -> commentId, likedCommentIdSet::contains));
  }

  private Map<Long, Integer> toIntMap(Map<Long, Long> counts) {
    return counts.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().intValue()));
  }
}
