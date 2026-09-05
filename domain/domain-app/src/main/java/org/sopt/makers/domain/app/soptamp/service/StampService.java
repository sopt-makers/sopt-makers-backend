package org.sopt.makers.domain.app.soptamp.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampFileStoragePort;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StampService {

  private final StampRepositoryPort stampRepositoryPort;
  private final StampFileStoragePort stampFileStoragePort;

  public List<Stamp> getAllByUserId(Long userId) {
    return stampRepositoryPort.findAllByUserId(userId);
  }

  public Stamp findStamp(Long missionId, Long userId) {
    Stamp stamp =
        stampRepositoryPort
            .findByUserIdAndMissionId(userId, missionId)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_STAMP));
    validate(stamp);
    return stamp;
  }

  @Transactional
  public Stamp register(
      Long userId, Long missionId, String contents, String image, String activityDate) {
    validateRegisterRequest(missionId, contents, image, activityDate);
    checkDuplicateStamp(userId, missionId);
    return stampRepositoryPort.save(Stamp.create(userId, missionId, contents, image, activityDate));
  }

  @Transactional
  public Stamp edit(
      Long userId, Long missionId, String contents, String image, String activityDate) {
    if (activityDate == null) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_ACTIVITY_DATE);
    }
    Stamp stamp = findStamp(missionId, userId);
    String newContents = contents != null && !contents.isBlank() ? contents : stamp.contents();
    List<String> newImages = image != null && !image.isBlank() ? List.of(image) : stamp.images();
    return stampRepositoryPort.updateContents(stamp.id(), newContents, newImages, activityDate);
  }

  @Transactional
  public void delete(Stamp stamp) {
    stampRepositoryPort.deleteById(stamp.id());
    stampFileStoragePort.deleteAll(stamp.images());
  }

  @Transactional
  public void deleteAll(Long userId) {
    List<Stamp> stamps = stampRepositoryPort.findAllByUserId(userId);
    stampRepositoryPort.deleteAllByUserId(userId);
    stamps.forEach(stamp -> stampFileStoragePort.deleteAll(stamp.images()));
  }

  public void checkDuplicateStamp(Long userId, Long missionId) {
    if (stampRepositoryPort.findByUserIdAndMissionId(userId, missionId).isPresent()) {
      throw new SoptampException(SoptampFailure.DUPLICATE_STAMP);
    }
  }

  public void checkDuplicateStampByTeam(List<Long> teamUserIds, Long missionId) {
    if (stampRepositoryPort.existsByUserIdInAndMissionId(teamUserIds, missionId)) {
      throw new SoptampException(SoptampFailure.DUPLICATE_STAMP);
    }
  }

  @Transactional
  public void increaseViewCount(Long stampId) {
    stampRepositoryPort.increaseViewCount(stampId);
  }

  public Stamp getById(Long stampId) {
    return stampRepositoryPort
        .findById(stampId)
        .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_STAMP));
  }

  public Stamp getOwnedStamp(Long stampId, Long userId) {
    return stampRepositoryPort
        .findByIdAndUserId(stampId, userId)
        .orElseThrow(() -> new SoptampException(SoptampFailure.STAMP_DELETE_FORBIDDEN));
  }

  private void validateRegisterRequest(
      Long missionId, String contents, String image, String activityDate) {
    if (missionId == null) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_MISSION_ID);
    }
    if (contents == null || contents.isBlank()) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_CONTENTS);
    }
    if (image == null || image.isBlank()) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_IMAGES);
    }
    if (activityDate == null) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_ACTIVITY_DATE);
    }
  }

  private void validate(Stamp stamp) {
    if (stamp.contents() == null || stamp.contents().isBlank()) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_CONTENTS);
    }
    if (stamp.images() == null || stamp.images().isEmpty()) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_IMAGES);
    }
    if (stamp.activityDate() == null) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_ACTIVITY_DATE);
    }
    if (Objects.isNull(stamp.missionId())) {
      throw new SoptampException(SoptampFailure.INVALID_STAMP_MISSION_ID);
    }
  }
}
