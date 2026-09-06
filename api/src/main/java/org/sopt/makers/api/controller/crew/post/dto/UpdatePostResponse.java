package org.sopt.makers.api.controller.crew.post.dto;

import java.util.List;
import org.sopt.makers.domain.playground.post.Post;

public record UpdatePostResponse(
    Long id, String title, String contents, String updatedDate, List<String> images) {

  public static UpdatePostResponse from(Post post) {
    return new UpdatePostResponse(
        post.id(), post.title(), post.contents(), String.valueOf(post.updatedAt()), post.images());
  }
}
