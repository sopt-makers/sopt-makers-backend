package org.sopt.makers.storage.db.playground.project.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.ProjectMember;
import org.sopt.makers.domain.playground.project.port.ProjectMemberRepositoryPort;
import org.sopt.makers.storage.db.playground.project.entity.ProjectMemberEntity;
import org.sopt.makers.storage.db.playground.project.repository.ProjectMemberJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectMemberRepositoryAdapter implements ProjectMemberRepositoryPort {

  private final ProjectMemberJpaRepository projectMemberJpaRepository;

  @Override
  public void saveAll(List<ProjectMember> members) {
    projectMemberJpaRepository.saveAll(members.stream().map(ProjectMemberEntity::from).toList());
  }

  @Override
  public List<ProjectMember> findAllByProjectId(Long projectId) {
    return projectMemberJpaRepository.findAllByProjectId(projectId).stream()
        .map(ProjectMemberEntity::toDomain)
        .toList();
  }

  @Override
  public void deleteAllByProjectId(Long projectId) {
    projectMemberJpaRepository.deleteAllByProjectId(projectId);
  }

  @Override
  public void deleteAll(List<ProjectMember> members) {
    List<Long> ids = members.stream().map(ProjectMember::id).toList();
    projectMemberJpaRepository.deleteAllById(ids);
  }
}
