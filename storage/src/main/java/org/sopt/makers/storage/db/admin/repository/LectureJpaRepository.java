package org.sopt.makers.storage.db.admin.repository;

import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureJpaRepository extends JpaRepository<LectureEntity, Long> {}
