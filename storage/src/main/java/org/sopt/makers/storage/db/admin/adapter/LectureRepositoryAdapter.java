package org.sopt.makers.storage.db.admin.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.port.LectureRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.sopt.makers.storage.db.admin.repository.LectureJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubLectureJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureRepositoryAdapter implements LectureRepositoryPort {

  private final LectureJpaRepository lectureJpaRepository;
  private final SubLectureJpaRepository subLectureJpaRepository;

  @Override
  @Transactional
  public Lecture save(
      String name,
      Part part,
      int generation,
      String place,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LectureAttribute attribute,
      LectureStatus status) {
    LectureEntity entity =
        LectureEntity.builder()
            .name(name)
            .part(part)
            .generation(generation)
            .place(place)
            .startDate(startDate)
            .endDate(endDate)
            .attribute(attribute)
            .status(status)
            .build();
    LectureEntity saved = lectureJpaRepository.save(entity);
    return toLecture(saved, List.of());
  }

  @Override
  public Optional<Lecture> findById(Long lectureId) {
    return lectureJpaRepository
        .findById(lectureId)
        .map(
            entity -> {
              List<SubLecture> subLectures =
                  subLectureJpaRepository.findAllByLectureId(entity.getId()).stream()
                      .map(SubLectureEntity::toDomain)
                      .toList();
              return toLecture(entity, subLectures);
            });
  }

  @Override
  public List<Lecture> findAllByGenerationAndPart(int generation, Part part) {
    return lectureJpaRepository.findAllByGenerationAndPart(generation, part).stream()
        .map(
            entity -> {
              List<SubLecture> subLectures =
                  subLectureJpaRepository.findAllByLectureId(entity.getId()).stream()
                      .map(SubLectureEntity::toDomain)
                      .toList();
              return toLecture(entity, subLectures);
            })
        .toList();
  }

  @Override
  @Transactional
  public void updateStatus(Long lectureId, LectureStatus status) {
    lectureJpaRepository.updateStatus(lectureId, status);
  }

  @Override
  @Transactional
  public void deleteById(Long lectureId) {
    lectureJpaRepository.deleteById(lectureId);
  }

  private Lecture toLecture(LectureEntity entity, List<SubLecture> subLectures) {
    return new Lecture(
        entity.getId(),
        entity.getName(),
        entity.getPart(),
        entity.getGeneration(),
        entity.getPlace(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getAttribute(),
        entity.getStatus(),
        subLectures);
  }
}
