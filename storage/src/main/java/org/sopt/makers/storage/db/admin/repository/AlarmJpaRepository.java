package org.sopt.makers.storage.db.admin.repository;

import org.sopt.makers.storage.db.admin.entity.AlarmEntity;
import org.sopt.makers.storage.db.admin.querydsl.AlarmQuerydslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmJpaRepository extends JpaRepository<AlarmEntity, Long>, AlarmQuerydslRepository {}
