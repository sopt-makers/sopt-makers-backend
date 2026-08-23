package org.sopt.makers.domain.playground.project.service;

import static org.sopt.makers.domain.playground.project.exception.ProjectFailure.EXCEEDED_IMAGE_COUNT;
import static org.sopt.makers.domain.playground.project.exception.ProjectFailure.NOT_FOUND_PROJECT;
import static org.sopt.makers.domain.playground.project.exception.ProjectFailure.UNAUTHORIZED_PROJECT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.ProjectLink;
import org.sopt.makers.domain.playground.project.ProjectMember;
import org.sopt.makers.domain.playground.project.exception.ProjectException;
import org.sopt.makers.domain.playground.project.port.ProjectLinkRepositoryPort;
import org.sopt.makers.domain.playground.project.port.ProjectMemberRepositoryPort;
import org.sopt.makers.domain.playground.project.port.ProjectQueryPort;
import org.sopt.makers.domain.playground.project.port.ProjectRepositoryPort;
import org.sopt.makers.domain.playground.project.port.ProjectUserPort;
import org.sopt.makers.domain.playground.project.port.ProjectUserPort.ProjectUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("playgroundProjectService")
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectRepositoryPort projectRepositoryPort;
  private final ProjectMemberRepositoryPort projectMemberRepositoryPort;
  private final ProjectLinkRepositoryPort projectLinkRepositoryPort;
  private final ProjectQueryPort projectQueryPort;
  private final ProjectUserPort projectUserPort;

  @Transactional
  public void createProject(
      String name,
      Long writerId,
      Integer generation,
      String category,
      LocalDate startAt,
      LocalDate endAt,
      List<String> serviceType,
      Boolean isAvailable,
      Boolean isFounding,
      String summary,
      String detail,
      String logoImage,
      String thumbnailImage,
      List<String> images,
      List<ProjectMember> members,
      List<ProjectLink> links) {
    validateImageCount(images.size());
    Project project =
        projectRepositoryPort.save(
            new Project(
                null,
                writerId,
                name,
                generation,
                category,
                startAt,
                endAt,
                serviceType,
                isAvailable,
                isFounding,
                summary,
                detail,
                logoImage,
                thumbnailImage,
                images,
                LocalDateTime.now(),
                LocalDateTime.now()));
    projectMemberRepositoryPort.saveAll(
        members.stream()
            .map(
                m ->
                    new ProjectMember(
                        null,
                        project.id(),
                        m.userId(),
                        m.role(),
                        m.description(),
                        m.isTeamMember()))
            .toList());
    projectLinkRepositoryPort.saveAll(
        links.stream().map(l -> new ProjectLink(null, project.id(), l.title(), l.url())).toList());
  }

  @Transactional
  public void updateProject(
      Long writerId,
      Long projectId,
      String name,
      Integer generation,
      String category,
      LocalDate startAt,
      LocalDate endAt,
      List<String> serviceType,
      Boolean isAvailable,
      Boolean isFounding,
      String summary,
      String detail,
      String logoImage,
      String thumbnailImage,
      List<String> images,
      List<ProjectMember> members,
      List<ProjectLink> links) {
    validateImageCount(images.size());
    Project project = getProjectById(projectId);
    validateWriter(project, writerId);

    projectRepositoryPort.save(
        new Project(
            project.id(),
            project.writerId(),
            name != null ? name : project.name(),
            generation != null ? generation : project.generation(),
            category != null ? category : project.category(),
            startAt != null ? startAt : project.startAt(),
            endAt,
            serviceType != null ? serviceType : project.serviceType(),
            isAvailable != null ? isAvailable : project.isAvailable(),
            isFounding != null ? isFounding : project.isFounding(),
            summary != null ? summary : project.summary(),
            detail != null ? detail : project.detail(),
            logoImage != null ? logoImage : project.logoImage(),
            thumbnailImage != null ? thumbnailImage : project.thumbnailImage(),
            images != null ? images : project.images(),
            project.createdAt(),
            LocalDateTime.now()));

    updateProjectMembers(projectId, members);
    updateProjectLinks(projectId, links);
  }

  @Transactional
  public void deleteProject(Long writerId, Long projectId) {
    Project project = getProjectById(projectId);
    validateWriter(project, writerId);
    projectLinkRepositoryPort.deleteAllByProjectId(projectId);
    projectMemberRepositoryPort.deleteAllByProjectId(projectId);
    projectRepositoryPort.delete(project);
  }

  @Transactional(readOnly = true)
  public List<Project> fetchAll(
      Integer limit,
      Long cursor,
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation) {
    return projectQueryPort.findProjects(
        limit, cursor, searchWord, category, isAvailable, isFounding, generation);
  }

  @Transactional(readOnly = true)
  public List<ProjectLink> getProjectLinks(List<Long> projectIds) {
    return projectLinkRepositoryPort.findAllByProjectIdIn(projectIds);
  }

  @Transactional(readOnly = true)
  public ProjectDetailResult getProjectDetail(Long projectId) {
    Project project = getProjectById(projectId);
    List<ProjectMember> projectMembers = projectMemberRepositoryPort.findAllByProjectId(projectId);
    List<ProjectLink> projectLinks = projectLinkRepositoryPort.findAllByProjectId(projectId);

    List<Long> userIds = projectMembers.stream().map(ProjectMember::userId).toList();
    Map<Long, ProjectUserInfo> userInfoMap =
        projectUserPort.getProjectUserInfosByIds(userIds).stream()
            .collect(Collectors.toMap(ProjectUserInfo::id, Function.identity()));

    List<ProjectMemberResult> memberResults =
        projectMembers.stream()
            .map(
                member -> {
                  ProjectUserInfo info = userInfoMap.get(member.userId());
                  if (info == null) {
                    return null;
                  }
                  return new ProjectMemberResult(
                      member.userId(),
                      member.role(),
                      member.description(),
                      member.isTeamMember(),
                      info.name(),
                      info.generations(),
                      info.profileImage(),
                      info.hasProfile());
                })
            .filter(Objects::nonNull)
            .toList();

    return new ProjectDetailResult(project, memberResults, projectLinks);
  }

  @Transactional(readOnly = true)
  public int getProjectsCount(
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation) {
    return projectQueryPort.countAllProjects(
        searchWord, category, isAvailable, isFounding, generation);
  }

  @Transactional(readOnly = true)
  public int getProjectCountByMemberId(Long memberId) {
    return projectQueryPort.countProjectsExcludeSopkathon(memberId);
  }

  @Transactional(readOnly = true)
  public List<Project> getRandomProjects() {
    return projectQueryPort.findRandomProjects(4);
  }

  private Project getProjectById(Long projectId) {
    return projectRepositoryPort
        .findById(projectId)
        .orElseThrow(() -> new ProjectException(NOT_FOUND_PROJECT));
  }

  private void validateImageCount(int imageCount) {
    if (imageCount > 10) {
      throw new ProjectException(EXCEEDED_IMAGE_COUNT);
    }
  }

  private void validateWriter(Project project, Long writerId) {
    if (!Objects.equals(project.writerId(), writerId)) {
      throw new ProjectException(UNAUTHORIZED_PROJECT);
    }
  }

  private void updateProjectMembers(Long projectId, List<ProjectMember> requestMembers) {
    List<ProjectMember> existing = projectMemberRepositoryPort.findAllByProjectId(projectId);
    Map<Long, ProjectMember> existingMap =
        existing.stream().collect(Collectors.toMap(ProjectMember::userId, Function.identity()));

    List<Long> requestedUserIds = requestMembers.stream().map(ProjectMember::userId).toList();
    List<ProjectMember> toRemove =
        existing.stream().filter(m -> !requestedUserIds.contains(m.userId())).toList();
    projectMemberRepositoryPort.deleteAll(toRemove);

    List<ProjectMember> toSave =
        requestMembers.stream()
            .map(
                m -> {
                  ProjectMember current = existingMap.get(m.userId());
                  if (current != null) {
                    return new ProjectMember(
                        current.id(),
                        projectId,
                        m.userId(),
                        m.role() != null ? m.role() : current.role(),
                        m.description() != null ? m.description() : current.description(),
                        m.isTeamMember() != null ? m.isTeamMember() : current.isTeamMember());
                  }
                  return new ProjectMember(
                      null, projectId, m.userId(), m.role(), m.description(), m.isTeamMember());
                })
            .toList();
    projectMemberRepositoryPort.saveAll(toSave);
  }

  private void updateProjectLinks(Long projectId, List<ProjectLink> requestLinks) {
    projectLinkRepositoryPort.deleteAllByProjectId(projectId);
    projectLinkRepositoryPort.saveAll(
        requestLinks.stream()
            .map(l -> new ProjectLink(null, projectId, l.title(), l.url()))
            .toList());
  }

  public record ProjectDetailResult(
      Project project, List<ProjectMemberResult> members, List<ProjectLink> links) {}

  public record ProjectMemberResult(
      Long userId,
      String role,
      String description,
      Boolean isTeamMember,
      String name,
      List<Integer> generations,
      String profileImage,
      boolean hasProfile) {}
}
