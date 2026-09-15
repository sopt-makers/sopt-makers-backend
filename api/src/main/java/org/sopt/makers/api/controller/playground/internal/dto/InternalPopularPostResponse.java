package org.sopt.makers.api.controller.playground.internal.dto;

import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService.InternalPopularPostSummary;

public record InternalPopularPostResponse(
    Long id,
    Long userId,
    String profileImage,
    String name,
    String generationAndPart,
    int rank,
    String category,
    String title,
    String content,
    String webLink) {

  public static InternalPopularPostResponse from(InternalPopularPostSummary summary) {
    return new InternalPopularPostResponse(
        summary.id(),
        summary.userId(),
        summary.profileImage(),
        summary.name(),
        summary.generationAndPart(),
        summary.rank(),
        summary.categoryName(),
        summary.title(),
        summary.content(),
        summary.webLink());
  }
}
