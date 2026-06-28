package org.sopt.makers.clients.playground.dto;

import java.util.List;

public record PlaygroundProjectResponse(
    Long id,
    String name,
    Integer generation,
    String category,
    List<String> serviceType,
    String summary,
    String logoImage,
    String thumbnailImage,
    boolean isFounding,
    List<ProjectLinkDto> links) {
  public record ProjectLinkDto(Long linkId, String linkTitle, String linkUrl) {}
}
