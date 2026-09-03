package org.sopt.makers.api.controller.internal.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.service.PostService;

public record InternalPostResponse(
    Long id,
    String title,
    String contents,
    LocalDateTime createdDate,
    List<String> images,
    InternalPostWriterResponse user,
    int likeCount,
    Boolean isLiked,
    int viewCount,
    int commentCount,
    Long meetingId,
    String meetingTitle,
    String category) {

  public static InternalPostResponse from(PostService.PostView view) {
    Post post = view.post();
    return new InternalPostResponse(
        post.id(),
        post.title(),
        post.contents(),
        post.createdAt(),
        post.images(),
        InternalPostWriterResponse.from(view.writer()),
        post.likeCount(),
        view.liked(),
        post.viewCount(),
        post.commentCount(),
        view.meeting().meetingId(),
        view.meeting().title(),
        view.meeting().category());
  }
}
