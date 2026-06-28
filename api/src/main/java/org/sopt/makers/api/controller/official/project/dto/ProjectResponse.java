package org.sopt.makers.api.controller.official.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.service.ProjectService;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectLinkType;
import org.sopt.makers.domain.official.project.type.ProjectMemberRole;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;

public class ProjectResponse {

  public record ProjectList(List<ProjectItem> projects, int total) {
    public static ProjectList of(ProjectService.ProjectsResult result) {
      return new ProjectList(
          result.projects().stream().map(ProjectItem::of).toList(), result.total());
    }
  }

  public record ProjectItem(
      Long id,
      String name,
      Integer generation,
      ProjectCategory category,
      List<ProjectServiceType> serviceType,
      String summary,
      String logoImage,
      String thumbnailImage,
      boolean isFounding,
      List<LinkItem> links) {
    public static ProjectItem of(Project p) {
      return new ProjectItem(
          p.id(),
          p.name(),
          p.generation(),
          p.category(),
          p.serviceType(),
          p.summary(),
          p.logoImage(),
          p.thumbnailImage(),
          p.isFounding(),
          p.links() == null
              ? List.of()
              : p.links().stream().map(l -> new LinkItem(l.linkType(), l.linkUrl())).toList());
    }
  }

  public record ProjectDetailItem(
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
      List<LinkItem> links,
      List<MemberItem> members) {
    public static ProjectDetailItem of(ProjectDetail pd) {
      return new ProjectDetailItem(
          pd.id(),
          pd.name(),
          pd.generation(),
          pd.category(),
          pd.serviceType(),
          pd.summary(),
          pd.detail(),
          pd.logoImage(),
          pd.thumbnailImage(),
          pd.projectImage(),
          pd.isFounding(),
          pd.startAt(),
          pd.endAt(),
          pd.uploadedAt(),
          pd.updatedAt(),
          pd.links() == null
              ? List.of()
              : pd.links().stream().map(l -> new LinkItem(l.linkType(), l.linkUrl())).toList(),
          pd.members() == null
              ? List.of()
              : pd.members().stream()
                  .map(m -> new MemberItem(m.name(), m.role(), m.description()))
                  .toList());
    }
  }

  public record LinkItem(ProjectLinkType linkType, String linkUrl) {}

  public record MemberItem(String name, ProjectMemberRole role, String description) {}
}
