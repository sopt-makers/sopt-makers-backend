package org.sopt.makers.api.controller.crew.post.dto;

import org.sopt.makers.domain.playground.post.PostWriter;

public record PostWriterResponse(Long id, Long orgId, String name, String profileImage) {

  public static PostWriterResponse from(PostWriter writer) {
    return writer == null
        ? new PostWriterResponse(null, null, null, null)
        : new PostWriterResponse(writer.id(), writer.id(), writer.name(), writer.profileImage());
  }
}
