package org.sopt.makers.storage.db.playground.project.repository;

import org.sopt.makers.storage.db.playground.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
}
