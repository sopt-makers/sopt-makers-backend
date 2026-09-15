package org.sopt.makers.domain.playground.post.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.comment.PostComment;

public interface PostCommentRepositoryPort {

  PostComment save(PostComment comment);

  Optional<PostComment> findById(Long commentId);

  Optional<PostComment> findByIdForUpdate(Long commentId);

  PageResult<PostComment> findParents(Long postId, PageQuery pageQuery);

  List<PostComment> findReplies(List<Long> parentCommentIds);

  int findMaxReplyOrder(Long parentCommentId);

  List<Long> findDistinctWriterIdsByPostId(Long postId);
}
