package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.post.Post;

public record HotPostResponse(Long id, String title, String content) {

  public static HotPostResponse of(Post post) {
    return new HotPostResponse(post.id(), post.title(), post.content());
  }
}
