package org.sopt.makers.domain.playground.post.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.port.PlaygroundPostQueryPort;
import org.sopt.makers.domain.playground.post.service.PostService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundPostQueryAdapter implements PlaygroundPostQueryPort {

  private final PostService postService;

  @Override
  public PageResult<PostInfo> findPosts(Long userId, int page, int take) {
    return postService
        .findMeetingPosts(null, userId, page, take)
        .map(view -> new PostInfo(view.post(), view.writer(), view.meeting(), view.liked()));
  }
}
