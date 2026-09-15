package org.sopt.makers.api.controller.playground.community.dto;

import java.util.List;
import org.sopt.makers.api.common.util.RelativeTimeFormatter;
import org.sopt.makers.domain.playground.community.post.SopticlePost;

public record SopticlePostResponse(
    Long id,
    MemberResponse member,
    String createdAt,
    String title,
    String content,
    List<String> images,
    String sopticleUrl) {

  public static SopticlePostResponse from(SopticlePost post) {
    return new SopticlePostResponse(
        post.id(),
        MemberResponse.from(post.member()),
        RelativeTimeFormatter.format(post.createdAt()),
        post.title(),
        post.content(),
        post.images(),
        post.sopticleUrl());
  }
}
