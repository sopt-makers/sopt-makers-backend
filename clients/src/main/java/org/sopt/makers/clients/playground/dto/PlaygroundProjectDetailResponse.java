package org.sopt.makers.clients.playground.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PlaygroundProjectDetailResponse(
    Long id,
    String name,
    Integer generation,
    String category,
    List<String> serviceType,
    String summary,
    String detail,
    String logoImage,
    String thumbnailImage,
    List<String> images,
    boolean isFounding,
    LocalDate startAt,
    LocalDate endAt,
    LocalDateTime uploadedAt,
    LocalDateTime updatedAt,
    List<ProjectLinkDto> links,
    List<ProjectMemberDto> members) {
  public record ProjectLinkDto(Long linkId, String linkTitle, String linkUrl) {}

  public record ProjectMemberDto(
      Long memberId,
      String memberName,
      String memberRole,
      String memberDescription,
      boolean isTeamMember,
      List<Integer> memberGenerations,
      String memberProfileImage,
      boolean memberHasProfile) {}
}
