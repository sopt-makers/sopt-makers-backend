package org.sopt.makers.storage.db.admin.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.port.SubLectureRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.sopt.makers.storage.db.admin.repository.SubLectureJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubLectureRepositoryAdapter implements SubLectureRepositoryPort {

  private final SubLectureJpaRepository subLectureJpaRepository;

  @Override
  public Optional<SubLecture> findById(Long id) {
    return subLectureJpaRepository.findByIdWithLecture(id).map(SubLectureEntity::toDomain);
  }
}
