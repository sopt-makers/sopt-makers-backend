package org.sopt.makers.domain.playground.community.comment.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.comment.CommentLike;

public interface CommentLikeRepositoryPort {

  CommentLike save(CommentLike commentLike);

  void delete(CommentLike commentLike);

  boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);

  Optional<CommentLike> findByMemberIdAndCommentId(Long memberId, Long commentId);

  int countAllByCommentId(Long commentId);

  List<Long> findLikedCommentIdsByMemberIdAndCommentIds(Long memberId, List<Long> commentIds);

  Map<Long, Long> countLikesByCommentIds(List<Long> commentIds);

  void deleteAllByCommentIds(List<Long> commentIds);
}
