package org.sopt.makers.storage.db.admin.adapter;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.port.SubLecturePort;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.sopt.makers.storage.db.admin.repository.LectureJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubLectureJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubLectureAdapter implements SubLecturePort {

  private final SubLectureJpaRepository subLectureJpaRepository;
  private final LectureJpaRepository lectureJpaRepository;

  @Override
  @Transactional
  public void saveAll(Long lectureId, List<Integer> rounds) {
    LectureEntity lecture = lectureJpaRepository.getReferenceById(lectureId);
    List<SubLectureEntity> entities =
        rounds.stream().map(round -> SubLectureEntity.create(lecture, round)).toList();
    subLectureJpaRepository.saveAll(entities);
  }

  @Override
  public List<SubLecture> findAllByLectureId(Long lectureId) {
    return subLectureJpaRepository.findAllByLectureId(lectureId).stream()
        .map(SubLectureEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public void updateCodeAndStartAt(Long subLectureId, String code, LocalDateTime startAt) {
    subLectureJpaRepository.updateCodeAndStartAt(subLectureId, code, startAt);
  }

  @Override
  @Transactional
  public void deleteAllByLectureId(Long lectureId) {
    subLectureJpaRepository.deleteAllByLectureId(lectureId);
  }
}
