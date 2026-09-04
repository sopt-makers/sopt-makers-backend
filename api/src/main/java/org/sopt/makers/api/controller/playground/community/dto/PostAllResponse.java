package org.sopt.makers.api.controller.playground.community.dto;

import java.util.List;
import org.sopt.makers.domain.playground.community.CommunityPostListCategory;
import org.sopt.makers.domain.playground.community.post.PostFeedResult;

public record PostAllResponse(
    CommunityPostListCategory category, Boolean hasNext, String nextCursor, List<PostResponse> posts) {

  public static PostAllResponse from(PostFeedResult result) {
    return new PostAllResponse(
        result.category(),
        result.hasNext(),
        result.nextCursor(),
        result.items().stream().map(PostResponse::from).toList());
  }
}
