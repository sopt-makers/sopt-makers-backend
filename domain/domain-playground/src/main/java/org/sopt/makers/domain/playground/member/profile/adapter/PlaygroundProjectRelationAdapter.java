package org.sopt.makers.domain.playground.member.profile.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundProjectRelationPort;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.ProjectMember;
import org.sopt.makers.domain.playground.project.port.ProjectMemberRepositoryPort;
import org.sopt.makers.domain.playground.project.port.ProjectQueryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundProjectRelationAdapter implements PlaygroundProjectRelationPort {

  private final ProjectQueryPort projectQueryPort;
  private final ProjectMemberRepositoryPort projectMemberRepositoryPort;

  @Override
  public List<Project> findProjectsByUserId(Long userId) {
    return projectQueryPort.findProjectsByUserId(userId);
  }

  @Override
  public List<Long> findUserIdsByProjectIds(List<Long> projectIds) {
    if (projectIds == null || projectIds.isEmpty()) {
      return List.of();
    }
    return projectMemberRepositoryPort.findAllByProjectIds(projectIds).stream()
        .map(ProjectMember::userId)
        .distinct()
        .toList();
  }
}
