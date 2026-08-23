package org.sopt.makers.domain.official.project.adapter;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.Project.ProjectLink;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.ProjectDetail.ProjectMember;
import org.sopt.makers.domain.official.project.exception.ProjectException;
import org.sopt.makers.domain.official.project.exception.ProjectFailure;
import org.sopt.makers.domain.official.project.port.ProjectClientPort;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectLinkType;
import org.sopt.makers.domain.official.project.type.ProjectMemberRole;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort.OfficialProjectDetailInfo;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort.OfficialProjectInfo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaygroundInternalProjectAdapter implements ProjectClientPort {

  private final OfficialProjectPort officialProjectPort;

  @Override
  public List<Project> fetchAll() {
    return officialProjectPort.fetchAll().stream().map(this::toProject).toList();
  }

  @Override
  public ProjectDetail fetchDetail(Long projectId) {
    try {
      OfficialProjectDetailInfo info = officialProjectPort.fetchDetail(projectId);
      return toProjectDetail(info);
    } catch (org.sopt.makers.domain.playground.project.exception.ProjectException e) {
      throw new ProjectException(ProjectFailure.PROJECT_NOT_FOUND);
    }
  }

  private Project toProject(OfficialProjectInfo info) {
    return new Project(
        info.id(),
        info.name(),
        info.generation(),
        parseCategory(info.category()),
        parseServiceTypes(info.serviceType()),
        info.summary(),
        info.logoImage(),
        info.thumbnailImage(),
        info.isFounding(),
        info.links().stream()
            .map(l -> new ProjectLink(parseLinkType(l.linkTitle()), l.linkUrl()))
            .toList());
  }

  private ProjectDetail toProjectDetail(OfficialProjectDetailInfo info) {
    String projectImage =
        (info.images() != null && !info.images().isEmpty()) ? info.images().get(0) : null;

    List<ProjectDetail.ProjectLink> links =
        info.links().stream()
            .map(l -> new ProjectDetail.ProjectLink(parseLinkType(l.linkTitle()), l.linkUrl()))
            .toList();

    List<ProjectMember> members =
        info.members().stream()
            .map(m -> new ProjectMember(m.name(), parseMemberRole(m.role()), m.description()))
            .toList();

    return new ProjectDetail(
        info.id(),
        info.name(),
        info.generation(),
        parseCategory(info.category()),
        parseServiceTypes(info.serviceType()),
        info.summary(),
        info.detail(),
        info.logoImage(),
        info.thumbnailImage(),
        projectImage,
        info.isFounding(),
        info.startAt(),
        info.endAt(),
        info.createdAt(),
        info.updatedAt(),
        links,
        members);
  }

  private ProjectCategory parseCategory(String category) {
    if (category == null) return null;
    try {
      return ProjectCategory.valueOf(category);
    } catch (IllegalArgumentException e) {
      log.warn("알 수 없는 프로젝트 카테고리: {}", category);
      return null;
    }
  }

  private List<ProjectServiceType> parseServiceTypes(List<String> types) {
    if (types == null) return List.of();
    return types.stream()
        .map(
            t -> {
              try {
                return ProjectServiceType.valueOf(t);
              } catch (IllegalArgumentException e) {
                log.warn("알 수 없는 서비스 타입: {}", t);
                return null;
              }
            })
        .filter(Objects::nonNull)
        .toList();
  }

  private ProjectLinkType parseLinkType(String title) {
    if (title == null) return ProjectLinkType.WEBSITE;
    return switch (title) {
      case "website" -> ProjectLinkType.WEBSITE;
      case "googlePlay" -> ProjectLinkType.GOOGLE_PLAYSTORE;
      case "appStore" -> ProjectLinkType.APP_STORE;
      case "github" -> ProjectLinkType.GITHUB;
      case "media" -> ProjectLinkType.MEDIA;
      case "instagram" -> ProjectLinkType.INSTAGRAM;
      default -> ProjectLinkType.WEBSITE;
    };
  }

  private ProjectMemberRole parseMemberRole(String role) {
    if (role == null) return null;
    try {
      return ProjectMemberRole.valueOf(role);
    } catch (IllegalArgumentException e) {
      log.warn("알 수 없는 멤버 역할: {}", role);
      return null;
    }
  }
}
