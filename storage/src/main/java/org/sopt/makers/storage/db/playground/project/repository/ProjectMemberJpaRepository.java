package org.sopt.makers.storage.db.playground.project.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.project.entity.ProjectMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberEntity, Long> {

  List<ProjectMemberEntity> findAllByProjectId(Long projectId);

  void deleteAllByProjectId(Long projectId);
}
