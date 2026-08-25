package org.sopt.makers.storage.db.playground.project.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.port.ProjectRepositoryPort;
import org.sopt.makers.storage.db.playground.project.entity.ProjectEntity;
import org.sopt.makers.storage.db.playground.project.repository.ProjectJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

  private final ProjectJpaRepository projectJpaRepository;

  @Override
  public Project save(Project project) {
    return projectJpaRepository.save(ProjectEntity.from(project)).toDomain();
  }

  @Override
  public Optional<Project> findById(Long id) {
    return projectJpaRepository.findById(id).map(ProjectEntity::toDomain);
  }

  @Override
  public void delete(Project project) {
    projectJpaRepository.findById(project.id()).ifPresent(projectJpaRepository::delete);
  }
}
