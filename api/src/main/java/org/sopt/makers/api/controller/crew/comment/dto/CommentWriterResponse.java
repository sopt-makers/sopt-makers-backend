package org.sopt.makers.api.controller.crew.comment.dto;

import org.sopt.makers.domain.playground.post.PostWriter;

public record CommentWriterResponse(Long id, Long orgId, String name, String profileImage) {

  public static CommentWriterResponse from(PostWriter writer) {
    return writer == null
        ? new CommentWriterResponse(null, null, null, null)
        : new CommentWriterResponse(writer.id(), writer.id(), writer.name(), writer.profileImage());
  }
}
