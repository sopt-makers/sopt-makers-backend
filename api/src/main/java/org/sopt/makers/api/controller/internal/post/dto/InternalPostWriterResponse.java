package org.sopt.makers.api.controller.internal.post.dto;

import org.sopt.makers.domain.playground.post.PostWriter;

public record InternalPostWriterResponse(
    Long id, Long orgId, String name, String profileImage, InternalPostActivityResponse partInfo) {

  public static InternalPostWriterResponse from(PostWriter writer) {
    if (writer == null) {
      return new InternalPostWriterResponse(null, null, null, null, null);
    }
    InternalPostActivityResponse activity =
        writer.generation() == null
            ? null
            : new InternalPostActivityResponse(writer.part(), writer.generation());
    return new InternalPostWriterResponse(
        writer.id(), writer.id(), writer.name(), writer.profileImage(), activity);
  }
}
