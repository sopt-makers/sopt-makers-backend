package org.sopt.makers.domain.app.soptamp.facade;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.MissionCompleteness;
import org.sopt.makers.domain.app.soptamp.port.SoptampPointUpdaterPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.service.MissionService;
import org.sopt.makers.domain.app.soptamp.service.StampService;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampClapQueryPort;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampFileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SoptampFacade {

  private final StampService stampService;
  private final MissionService missionService;
  private final SoptampPointUpdaterPort soptampPointUpdaterPort;
  private final SoptampUserQueryPort soptampUserQueryPort;
  private final StampClapQueryPort stampClapQueryPort;
  private final StampFileStoragePort stampFileStoragePort;

  private static final String STAMP_IMAGE_DIRECTORY = "stamp";
  private static final String MISSION_IMAGE_DIRECTORY = "mission";
  private static final String DEFAULT_IMAGE_FILE_NAME = "image";
  private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/png";

  @Value("${makers.app.soptamp.report.url:}")
  private String formUrl;

  public record StampView(Stamp stamp, String ownerNickname, boolean mine, int myClapCount) {}

  public record SoptampReport(String reportUrl) {}

  public StampFileStoragePort.PresignedFile generateStampImagePresignedUrl() {
    return stampFileStoragePort.generatePresignedUrl(
        new StampFileStoragePort.PresignedFileRequest(
            DEFAULT_IMAGE_FILE_NAME, DEFAULT_IMAGE_CONTENT_TYPE, STAMP_IMAGE_DIRECTORY));
  }

  public StampFileStoragePort.PresignedFile generateMissionImagePresignedUrl() {
    return stampFileStoragePort.generatePresignedUrl(
        new StampFileStoragePort.PresignedFileRequest(
            DEFAULT_IMAGE_FILE_NAME, DEFAULT_IMAGE_CONTENT_TYPE, MISSION_IMAGE_DIRECTORY));
  }

  @Transactional
  public Stamp registerStamp(
      Long userId, Long missionId, String contents, String image, String activityDate) {
    Stamp stamp = stampService.register(userId, missionId, contents, image, activityDate);
    Mission mission = missionService.getById(missionId);
    soptampPointUpdaterPort.addPointByLevel(userId, mission.level());
    return stamp;
  }

  @Transactional
  public Stamp editStamp(
      Long userId, Long missionId, String contents, String image, String activityDate) {
    return stampService.edit(userId, missionId, contents, image, activityDate);
  }

  @Transactional
  public void deleteStamp(Long userId, Long stampId) {
    Stamp stamp = stampService.getOwnedStamp(stampId, userId);
    Mission mission = missionService.getById(stamp.missionId());
    soptampPointUpdaterPort.subtractPointByLevel(userId, mission.level());
    stampService.delete(stamp);
  }

  @Transactional
  public void deleteAllStamps(Long userId) {
    stampService.deleteAll(userId);
    soptampPointUpdaterPort.initPoint(userId);
  }

  @Transactional
  public StampView getStamp(Long viewerUserId, Long missionId, String ownerNickname) {
    SoptampUser owner =
        soptampUserQueryPort
            .findByNickname(ownerNickname)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
    Stamp stamp = stampService.findStamp(missionId, owner.userId());
    int myClapCount = stampClapQueryPort.getUserClapCount(viewerUserId, stamp.id());
    stampService.increaseViewCount(stamp.id());
    return new StampView(
        stamp.withViewCount(stamp.viewCount() + 1),
        owner.nickname(),
        Objects.equals(viewerUserId, owner.userId()),
        myClapCount);
  }

  public Mission registerMission(String title, Integer level, String image) {
    return missionService.register(title, level, image);
  }

  public List<MissionCompleteness> getAllMissionsWithCompleteness(Long userId) {
    return missionService.findAllWithCompleteness(userId);
  }

  public List<Mission> getCompletedMissions(Long userId) {
    return missionService.getCompleted(userId);
  }

  public List<Mission> getIncompleteMissions(Long userId) {
    return missionService.getIncomplete(userId);
  }

  public SoptampReport getReportUrl() {
    return new SoptampReport(formUrl);
  }
}
