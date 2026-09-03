package org.sopt.makers.api.controller.crew.comment.dto;

import java.util.List;
import org.sopt.makers.api.controller.crew.post.dto.PostPageMetaResponse;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.service.PostCommentService;

public record CommentPageResponse(List<CommentResponse> comments, PostPageMetaResponse meta) {

  public static CommentPageResponse from(PageResult<PostCommentService.CommentView> page) {
    return new CommentPageResponse(
        page.content().stream().map(CommentResponse::from).toList(),
        PostPageMetaResponse.from(page));
  }
}
