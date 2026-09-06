package org.sopt.makers.api.controller.crew.post.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.service.PostService;

public record PostPageResponse(List<PostListItemResponse> posts, PostPageMetaResponse meta) {

  public static PostPageResponse from(PageResult<PostService.PostView> page) {
    return new PostPageResponse(
        page.content().stream().map(PostListItemResponse::from).toList(),
        PostPageMetaResponse.from(page));
  }
}
