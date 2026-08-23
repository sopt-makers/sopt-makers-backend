package org.sopt.makers.storage.db.playground.project.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.project.entity.ProjectLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectLinkJpaRepository extends JpaRepository<ProjectLinkEntity, Long> {

  List<ProjectLinkEntity> findAllByProjectId(Long projectId);

  List<ProjectLinkEntity> findAllByProjectIdIn(List<Long> projectIds);

  void deleteAllByProjectId(Long projectId);
}
