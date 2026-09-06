package org.sopt.makers.api.controller.crew.post.dto;

import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.service.PostService;

public record MumuPostResponse(
    Long meetingId,
    String meetingTitle,
    String meetingCategory,
    Long postId,
    int likeCount,
    int commentCount,
    String title,
    String content,
    Boolean isLiked) {

  public static MumuPostResponse from(PostService.PostView view) {
    Post post = view.post();
    return new MumuPostResponse(
        view.meeting().meetingId(),
        view.meeting().title(),
        view.meeting().category(),
        post.id(),
        post.likeCount(),
        post.commentCount(),
        post.title(),
        post.contents(),
        view.liked());
  }
}
