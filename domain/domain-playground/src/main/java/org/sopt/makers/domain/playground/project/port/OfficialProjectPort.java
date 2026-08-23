package org.sopt.makers.domain.playground.project.port;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OfficialProjectPort {

  List<OfficialProjectInfo> fetchAll();

  OfficialProjectDetailInfo fetchDetail(Long projectId);

  record OfficialProjectInfo(
      Long id,
      String name,
      Integer generation,
      String category,
      List<String> serviceType,
      String summary,
      String logoImage,
      String thumbnailImage,
      boolean isFounding,
      List<OfficialProjectLinkInfo> links) {}

  record OfficialProjectDetailInfo(
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
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<OfficialProjectLinkInfo> links,
      List<OfficialProjectMemberInfo> members) {}

  record OfficialProjectLinkInfo(String linkTitle, String linkUrl) {}

  record OfficialProjectMemberInfo(String name, String role, String description) {}
}
