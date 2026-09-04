package org.sopt.makers.domain.playground.community.comment.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.comment.Comment;

public interface CommentRepositoryPort {

  Comment save(Comment comment);

  Optional<Comment> findById(Long commentId);

  boolean existsById(Long commentId);

  List<Comment> findAllByPostId(Long postId);

  List<Comment> findAllByPostIds(List<Long> postIds);

  List<Comment> findAllByParentCommentId(Long parentCommentId);

  Map<Long, Long> countNonDeletedByPostIds(List<Long> postIds);

  void deleteAllByPostId(Long postId);
}
