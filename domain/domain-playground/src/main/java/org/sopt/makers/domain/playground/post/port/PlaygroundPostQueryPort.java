package org.sopt.makers.domain.playground.post.port;

import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.MeetingPostContext;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostWriter;

/** Playground 모임 피드가 기존 {@code GET /internal/post/:orgId} 대신 사용하는 내부 조회 Port. */
public interface PlaygroundPostQueryPort {

  PageResult<PostInfo> findPosts(Long userId, int page, int take);

  record PostInfo(Post post, PostWriter writer, MeetingPostContext meeting, boolean liked) {}
}
