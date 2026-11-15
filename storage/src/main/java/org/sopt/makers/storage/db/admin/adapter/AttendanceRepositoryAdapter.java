package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureAttendanceDetail;
import org.sopt.makers.domain.admin.attendance.MemberAttendanceDetail;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubAttendanceJpaRepository;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.sopt.makers.storage.db.user.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceRepositoryAdapter implements AttendanceRepositoryPort {

  private final AttendanceJpaRepository attendanceJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final SubAttendanceJpaRepository subAttendanceJpaRepository;

  @Override
  public Optional<Attendance> findById(Long id) {
    return attendanceJpaRepository.findByIdWithLecture(id).map(this::toAttendanceDomain);
  }

  @Override
  public List<MemberAttendanceDetail> findAllDetailsByUserIdAndGeneration(
      Long userId, int generation) {
    List<AttendanceEntity> entities =
        attendanceJpaRepository.findAllByUserIdAndGeneration(userId, generation);
    if (entities.isEmpty()) {
      return List.of();
    }
    List<Long> ids = entities.stream().map(AttendanceEntity::getId).toList();
    Map<Long, List<SubAttendanceEntity>> subsByAttendanceId =
        subAttendanceJpaRepository.findAllByAttendanceIdIn(ids).stream()
            .collect(Collectors.groupingBy(sub -> sub.getAttendance().getId()));
    return entities.stream()
        .map(
            entity -> {
              List<SubAttendanceEntity> subs =
                  subsByAttendanceId.getOrDefault(entity.getId(), List.of());
              Attendance attendance = entity.toDomain(List.of());
              return new MemberAttendanceDetail(
                  attendance.lectureName(),
                  entity.getLecture().getStartDate(),
                  entity.getLecture().getEndDate(),
                  attendance.computeScore(),
                  attendance.status(),
                  subs.stream()
                      .map(
                          sub ->
                              new MemberAttendanceDetail.SubAttendanceDetail(
                                  sub.getSubLecture().getRound(),
                                  sub.getStatus(),
                                  sub.getUpdatedAt()))
                      .toList());
            })
        .toList();
  }

  @Override
  public Optional<Attendance> findByLectureIdAndUserId(Long lectureId, Long userId) {
    return attendanceJpaRepository
        .findByLectureIdAndUserId(lectureId, userId)
        .map(this::toAttendanceDomain);
  }

  @Override
  public List<Attendance> findAllEndedByUserId(Long userId, int generation) {
    return toBatchAttendanceDomain(
        attendanceJpaRepository.findAllEndedByUserId(userId, generation, LectureStatus.END));
  }

  @Override
  public List<Attendance> findAllEndedByUserIds(List<Long> userIds, int generation) {
    return toBatchAttendanceDomain(
        attendanceJpaRepository.findAllEndedByUserIds(userIds, generation, LectureStatus.END));
  }

  @Transactional
  @Override
  public void updateStatus(Long attendanceId, AttendanceStatus status) {
    attendanceJpaRepository.updateStatus(attendanceId, status);
  }

  @Override
  public List<LectureAttendanceDetail> findAllByLectureIdAndPart(
      Long lectureId, Part part, int page, int limit) {

    List<AttendanceEntity> entities =
        attendanceJpaRepository.findAllByLectureIdAndPart(
            lectureId, part, PageRequest.of(page, limit));

    if (entities.isEmpty()) return List.of();

    Map<Long, String> names =
        userJpaRepository
            .findAllById(entities.stream().map(AttendanceEntity::getUserId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(UserEntity::getId, UserEntity::getName));

    Map<Long, List<SubAttendanceEntity>> subs =
        subAttendanceJpaRepository
            .findAllByAttendanceIdIn(entities.stream().map(AttendanceEntity::getId).toList())
            .stream()
            .collect(Collectors.groupingBy(sub -> sub.getAttendance().getId()));

    return entities.stream()
        .map(
            entity ->
                new LectureAttendanceDetail(
                    entity.getId(),
                    entity.getUserId(),
                    names.get(entity.getUserId()),
                    entity.toDomain(List.of()).computeScore(),
                    subs.getOrDefault(entity.getId(), List.of()).stream()
                        .map(
                            sub ->
                                new LectureAttendanceDetail.SubAttendanceDetail(
                                    sub.getId(),
                                    sub.getSubLecture().getRound(),
                                    sub.getStatus(),
                                    sub.getUpdatedAt()))
                        .toList()))
        .toList();
  }

  @Override
  public int countByLectureIdAndPart(Long lectureId, Part part) {
    return attendanceJpaRepository.countByLectureIdAndPart(lectureId, part);
  }

  private List<Attendance> toBatchAttendanceDomain(List<AttendanceEntity> entities) {
    if (entities.isEmpty()) {
      return List.of();
    }
    List<Long> attendanceIds = entities.stream().map(AttendanceEntity::getId).toList();
    Map<Long, List<SubAttendanceEntity>> subsByAttendanceId =
        subAttendanceJpaRepository.findAllByAttendanceIdIn(attendanceIds).stream()
            .collect(Collectors.groupingBy(sa -> sa.getAttendance().getId()));
    return entities.stream()
        .map(
            entity -> {
              List<SubAttendance> subs =
                  subsByAttendanceId.getOrDefault(entity.getId(), List.of()).stream()
                      .map(SubAttendanceEntity::toDomain)
                      .toList();
              return entity.toDomain(subs);
            })
        .toList();
  }

  private Attendance toAttendanceDomain(AttendanceEntity entity) {
    List<SubAttendance> subs =
        subAttendanceJpaRepository.findByAttendanceIdWithSubLecture(entity.getId()).stream()
            .map(SubAttendanceEntity::toDomain)
            .toList();
    return entity.toDomain(subs);
  }
}
