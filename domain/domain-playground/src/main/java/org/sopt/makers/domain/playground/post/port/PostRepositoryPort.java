package org.sopt.makers.domain.playground.post.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostContentType;

public interface PostRepositoryPort {

  Post save(Post post);

  Optional<Post> findById(Long postId);

  Optional<Post> findByIdForUpdate(Long postId);

  PageResult<Post> findByMeetingId(Long meetingId, PageQuery pageQuery);

  PageResult<Post> findByMeetingIds(List<Long> meetingIds, PageQuery pageQuery);

  List<Post> findByMeetingIdsAndContentTypeAndCreatedAtBetweenExcludingWriter(
      List<Long> meetingIds,
      PostContentType contentType,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Long writerId);

  long countByMeetingId(Long meetingId);
}
