package org.sopt.makers.domain.playground.project.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.project.Project;

public interface ProjectRepositoryPort {

  Project save(Project project);

  Optional<Project> findById(Long id);

  void delete(Project project);
}
