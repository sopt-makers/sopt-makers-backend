package org.sopt.makers.domain.playground.community.comment.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.comment.CommentLike;

public interface CommentLikeRepositoryPort {

  CommentLike save(CommentLike commentLike);

  void delete(CommentLike commentLike);

  boolean existsByUserIdAndCommentId(Long userId, Long commentId);

  Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

  int countAllByCommentId(Long commentId);

  List<Long> findLikedCommentIdsByUserIdAndCommentIds(Long userId, List<Long> commentIds);

  Map<Long, Long> countLikesByCommentIds(List<Long> commentIds);

  void deleteAllByCommentIds(List<Long> commentIds);
}
