package org.sopt.makers.domain.official.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectLinkType;
import org.sopt.makers.domain.official.project.type.ProjectMemberRole;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;

public record ProjectDetail(
    Long id,
    String name,
    Integer generation,
    ProjectCategory category,
    List<ProjectServiceType> serviceType,
    String summary,
    String detail,
    String logoImage,
    String thumbnailImage,
    String projectImage,
    boolean isFounding,
    LocalDate startAt,
    LocalDate endAt,
    LocalDateTime uploadedAt,
    LocalDateTime updatedAt,
    List<ProjectLink> links,
    List<ProjectMember> members
) {
  public record ProjectLink(ProjectLinkType linkType, String linkUrl) {}

  public record ProjectMember(String name, ProjectMemberRole role, String description) {}
}
