package org.sopt.makers.api.controller.crew.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.domain.playground.post.service.PostService;

public record PostListItemResponse(
    Long id,
    String title,
    String contents,
    LocalDateTime createdDate,
    List<String> images,
    PostWriterResponse user,
    int likeCount,
    Boolean isLiked,
    int viewCount,
    int commentCount,
    PostMeetingResponse meeting,
    PostContentType category,
    List<String> commenterThumbnails,
    Boolean isBlockedPost) {

  public static PostListItemResponse from(PostService.PostView view) {
    Post post = view.post();
    return new PostListItemResponse(
        post.id(),
        post.title(),
        post.contents(),
        post.createdAt(),
        post.images(),
        PostWriterResponse.from(view.writer()),
        post.likeCount(),
        view.liked(),
        post.viewCount(),
        post.commentCount(),
        PostMeetingResponse.from(view.meeting()),
        post.contentType(),
        view.commenterThumbnails(),
        view.blocked());
  }
}
