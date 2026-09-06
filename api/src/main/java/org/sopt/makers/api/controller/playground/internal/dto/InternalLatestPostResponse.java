package org.sopt.makers.api.controller.playground.internal.dto;

import java.time.format.DateTimeFormatter;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService.InternalPostSummary;

public record InternalLatestPostResponse(
    Long id,
    Long userId,
    String profileImage,
    String name,
    String generationAndPart,
    String category,
    String title,
    String content,
    String webLink,
    String createdAt) {

  private static final DateTimeFormatter CREATED_AT_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

  public static InternalLatestPostResponse from(InternalPostSummary summary) {
    return new InternalLatestPostResponse(
        summary.id(),
        summary.userId(),
        summary.profileImage(),
        summary.name(),
        summary.generationAndPart(),
        summary.categoryName(),
        summary.title(),
        summary.content(),
        summary.webLink(),
        summary.createdAt().format(CREATED_AT_FORMATTER));
  }
}
