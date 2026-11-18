package org.sopt.makers.api.controller.official.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.api.controller.official.common.dto.OfficialPageResponse;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.service.ProjectService;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectLinkType;
import org.sopt.makers.domain.official.project.type.ProjectMemberRole;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;

public class ProjectResponse {

  public static OfficialPageResponse<ProjectItem> page(
      ProjectService.ProjectsResult result, int limit, int pageNo) {
    return OfficialPageResponse.of(
        result.projects().stream().map(ProjectItem::of).toList(), result.total(), limit, pageNo);
  }

  public record ProjectItem(
      Long id,
      String name,
      Integer generation,
      ProjectCategory category,
      List<ProjectServiceType> serviceType,
      String summary,
      String detail,
      String logoImage,
      String thumbnailImage,
      boolean isFounding,
      boolean isAvailable,
      List<LinkItem> links) {
    public static ProjectItem of(Project p) {
      return new ProjectItem(
          p.id(),
          p.name(),
          p.generation(),
          p.category(),
          p.serviceType(),
          p.summary(),
          p.detail(),
          p.logoImage(),
          p.thumbnailImage(),
          p.isFounding(),
          p.isAvailable(),
          p.links() == null
              ? List.of()
              : p.links().stream().map(l -> LinkItem.of(l.linkType(), l.linkUrl())).toList());
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
      @JsonInclude(JsonInclude.Include.NON_EMPTY) String projectImage,
      boolean isFounding,
      boolean isAvailable,
      LocalDate startAt,
      LocalDate endAt,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime uploadedAt,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime updatedAt,
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
          pd.isAvailable(),
          pd.startAt(),
          pd.endAt(),
          pd.uploadedAt(),
          pd.updatedAt(),
          pd.links() == null
              ? List.of()
              : pd.links().stream().map(l -> LinkItem.of(l.linkType(), l.linkUrl())).toList(),
          pd.members() == null
              ? List.of()
              : pd.members().stream()
                  .map(m -> MemberItem.of(m.name(), m.role(), m.description()))
                  .toList());
    }
  }

  /** 링크 종류는 FE가 사용하는 레거시 값(website, googlePlay 등)으로 응답한다. */
  public record LinkItem(String title, String url) {
    static LinkItem of(ProjectLinkType type, String url) {
      return new LinkItem(type.getValue(), url);
    }
  }

  /** 역할은 화면에 표시하는 역할명으로 응답한다. 알 수 없는 역할은 null이다. */
  public record MemberItem(String name, String role, String description) {
    static MemberItem of(String name, ProjectMemberRole role, String description) {
      return new MemberItem(name, role == null ? null : role.getDescription(), description);
    }
  }
}
