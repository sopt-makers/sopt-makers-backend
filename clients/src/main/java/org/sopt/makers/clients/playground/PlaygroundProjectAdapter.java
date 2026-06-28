package org.sopt.makers.clients.playground;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.playground.dto.PlaygroundProjectDetailResponse;
import org.sopt.makers.clients.playground.dto.PlaygroundProjectPageResponse;
import org.sopt.makers.clients.playground.dto.PlaygroundProjectResponse;
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
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
public class PlaygroundProjectAdapter implements ProjectClientPort {

  private static final int PAGE_SIZE = 20;

  private final PlaygroundClient client;

  public PlaygroundProjectAdapter(PlaygroundProperty property) {
    this.client = new PlaygroundClient(property);
  }

  @Override
  public List<Project> fetchAll() {
    Map<String, Project> byName = new LinkedHashMap<>();
    int cursor = 0;
    boolean hasNext = true;
    while (hasNext) {
      PlaygroundProjectPageResponse page = client.getAllProjects(PAGE_SIZE, cursor);
      if (page == null || page.projectList().isEmpty()) break;
      for (PlaygroundProjectResponse item : page.projectList()) {
        byName.putIfAbsent(item.name(), toProject(item));
      }
      hasNext = page.hasNext();
      cursor += page.projectList().size();
    }
    return new ArrayList<>(byName.values());
  }

  @Override
  public ProjectDetail fetchDetail(Long projectId) {
    try {
      PlaygroundProjectDetailResponse resp = client.getProjectDetail(projectId);
      if (resp == null) {
        throw new ProjectException(ProjectFailure.PROJECT_NOT_FOUND);
      }
      return toProjectDetail(resp);
    } catch (HttpClientErrorException.NotFound e) {
      throw new ProjectException(ProjectFailure.PROJECT_NOT_FOUND);
    }
  }

  private Project toProject(PlaygroundProjectResponse r) {
    return new Project(
        r.id(),
        r.name(),
        r.generation(),
        parseCategory(r.category()),
        parseServiceTypes(r.serviceType()),
        r.summary(),
        r.logoImage(),
        r.thumbnailImage(),
        r.isFounding(),
        r.links() == null
            ? List.of()
            : r.links().stream()
                .map(l -> new ProjectLink(parseLinkType(l.linkTitle()), l.linkUrl()))
                .toList());
  }

  private ProjectDetail toProjectDetail(PlaygroundProjectDetailResponse r) {
    String projectImage = (r.images() != null && !r.images().isEmpty()) ? r.images().get(0) : null;
    List<ProjectDetail.ProjectLink> links =
        r.links() == null
            ? List.of()
            : r.links().stream()
                .map(l -> new ProjectDetail.ProjectLink(parseLinkType(l.linkTitle()), l.linkUrl()))
                .toList();
    List<ProjectMember> members =
        r.members() == null
            ? List.of()
            : r.members().stream()
                .map(
                    m ->
                        new ProjectMember(
                            m.memberName(), parseMemberRole(m.memberRole()), m.memberDescription()))
                .toList();
    return new ProjectDetail(
        r.id(),
        r.name(),
        r.generation(),
        parseCategory(r.category()),
        parseServiceTypes(r.serviceType()),
        r.summary(),
        r.detail(),
        r.logoImage(),
        r.thumbnailImage(),
        projectImage,
        r.isFounding(),
        r.startAt(),
        r.endAt(),
        r.uploadedAt(),
        r.updatedAt(),
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
        .filter(java.util.Objects::nonNull)
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
