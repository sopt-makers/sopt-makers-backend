package org.sopt.makers.api.controller.internal.post.dto;

import java.util.List;
import org.sopt.makers.api.controller.crew.post.dto.PostPageMetaResponse;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.service.PostService;

public record InternalPostPageResponse(
    List<InternalPostResponse> posts, PostPageMetaResponse pageMeta) {

  public static InternalPostPageResponse from(PageResult<PostService.PostView> page) {
    return new InternalPostPageResponse(
        page.content().stream().map(InternalPostResponse::from).toList(),
        PostPageMetaResponse.from(page));
  }
}
