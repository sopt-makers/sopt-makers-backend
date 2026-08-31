package org.sopt.makers.domain.crew.advertisement.service;

import static org.sopt.makers.domain.crew.advertisement.AdvertisementCategory.MEETING_TOP;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.DUPLICATE_DISPLAYED_MEETING_TOP;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.INVALID_ADVERTISEMENT_CATEGORY;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.INVALID_ADVERTISEMENT_IMAGE;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.INVALID_ADVERTISEMENT_PERIOD;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.INVALID_ADVERTISEMENT_UPDATE;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.INVALID_ADVERTISEMENT_VALUE;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.INVALID_MEETING_TOP_ADVERTISEMENT;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.NOT_FOUND_ADVERTISEMENT;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.NOT_FOUND_ADVERTISEMENT_USER;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.crew.advertisement.Advertisement;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;
import org.sopt.makers.domain.crew.advertisement.AdvertisementEventType;
import org.sopt.makers.domain.crew.advertisement.AdvertisementTargetGeneration;
import org.sopt.makers.domain.crew.advertisement.MeetingTopAdvertisement;
import org.sopt.makers.domain.crew.advertisement.exception.AdvertisementException;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementActiveGenerationPort;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementImageStoragePort;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementRepositoryPort;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.user.Activity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

  private static final String SOPKATHON_APPLY_TITLE_FORMAT = "[%d기 솝커톤] %s 파트 신청";
  private static final String NETWORKING_TITLE_QUERY_FORMAT = "[%d기 네트워킹 데이]";
  private static final String MEETING_TOP_DIRECTORY = "meeting_top";
  private static final DateTimeFormatter DIRECTORY_DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyy/MM/dd");
  private static final Set<String> IMAGE_CONTENT_TYPES =
      Set.of("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

  private final AdvertisementRepositoryPort advertisementRepositoryPort;
  private final AdvertisementActiveGenerationPort activeGenerationPort;
  private final AdvertisementImageStoragePort imageStoragePort;
  private final MeetingRepositoryPort meetingRepositoryPort;
  private final MeetingUserPort meetingUserPort;
  private final Clock clock;

  public List<Advertisement> getGeneralAdvertisements(AdvertisementCategory category) {
    validateGeneralCategory(category);
    List<Advertisement> advertisements =
        advertisementRepositoryPort.findSponsoredInPeriod(
            category, LocalDateTime.now(clock), category.getMaxItems());
    return advertisements.isEmpty()
        ? advertisementRepositoryPort.findDefault(category, category.getMaxItems())
        : advertisements;
  }

  public Optional<MeetingTopAdvertisement> getMeetingTopAdvertisement(
      Long userId, AdvertisementEventType eventType) {
    if (eventType == null) {
      return Optional.empty();
    }
    return advertisementRepositoryPort.findDisplayedMeetingTop(LocalDateTime.now(clock)).stream()
        .filter(advertisement -> advertisement.eventType() == eventType)
        .map(advertisement -> createMeetingTopAdvertisement(advertisement, userId))
        .flatMap(Optional::stream)
        .findFirst();
  }

  @Transactional
  public Advertisement updateMeetingTopAdvertisement(
      Integer advertisementId, Advertisement.UpdateValues values) {
    advertisementRepositoryPort.lockAllByCategory(MEETING_TOP);
    Advertisement advertisement =
        advertisementRepositoryPort
            .findById(advertisementId)
            .orElseThrow(() -> new AdvertisementException(NOT_FOUND_ADVERTISEMENT));
    validateUpdate(advertisement, values);
    Advertisement updated = advertisement.update(values);
    validatePeriod(updated.startDate(), updated.endDate());
    if (Boolean.TRUE.equals(values.display())
        && !advertisement.display()
        && advertisementRepositoryPort.existsOtherDisplayed(MEETING_TOP, advertisementId)) {
      throw new AdvertisementException(DUPLICATE_DISPLAYED_MEETING_TOP);
    }
    return advertisementRepositoryPort.update(updated);
  }

  public String uploadMeetingTopImage(AdvertisementImageStoragePort.UploadImage image) {
    validateImage(image);
    String directory =
        MEETING_TOP_DIRECTORY + "/" + LocalDateTime.now(clock).format(DIRECTORY_DATE_FORMAT);
    return imageStoragePort.upload(image, directory);
  }

  private Optional<MeetingTopAdvertisement> createMeetingTopAdvertisement(
      Advertisement advertisement, Long userId) {
    return switch (advertisement.eventType()) {
      case SOPKATHON -> createSopkathonAdvertisement(advertisement, userId);
      case NETWORKING -> Optional.of(createNetworkingAdvertisement(advertisement));
    };
  }

  private Optional<MeetingTopAdvertisement> createSopkathonAdvertisement(
      Advertisement advertisement, Long userId) {
    MeetingUser user =
        meetingUserPort
            .findById(userId)
            .orElseThrow(() -> new AdvertisementException(NOT_FOUND_ADVERTISEMENT_USER));
    int activeGeneration = activeGenerationPort.getActiveGeneration();
    Optional<Activity> targetActivity = findTargetActivity(user, advertisement, activeGeneration);
    if (targetActivity.isEmpty()) {
      return Optional.empty();
    }
    Optional<MeetingJoinablePart> part = toJoinablePart(targetActivity.get().part());
    if (part.isEmpty()) {
      return Optional.empty();
    }
    String title =
        SOPKATHON_APPLY_TITLE_FORMAT.formatted(activeGeneration, part.get().getDisplayName());
    Long applicationMeetingId = meetingRepositoryPort.findFirstIdByTitle(title).orElse(null);
    return Optional.of(
        new MeetingTopAdvertisement(advertisement, activeGeneration, applicationMeetingId));
  }

  private MeetingTopAdvertisement createNetworkingAdvertisement(Advertisement advertisement) {
    int activeGeneration = activeGenerationPort.getActiveGeneration();
    String titleQuery = NETWORKING_TITLE_QUERY_FORMAT.formatted(activeGeneration);
    Long applicationMeetingId =
        meetingRepositoryPort.findFirstIdByTitleContaining(titleQuery).orElse(null);
    return new MeetingTopAdvertisement(advertisement, null, applicationMeetingId);
  }

  private Optional<Activity> findTargetActivity(
      MeetingUser user, Advertisement advertisement, int activeGeneration) {
    AdvertisementTargetGeneration target =
        advertisement.targetGeneration() == null
            ? AdvertisementTargetGeneration.ALL
            : advertisement.targetGeneration();
    return switch (target) {
      case ACTIVE -> findActiveActivity(user, activeGeneration);
      case RECENT -> findRecentActivity(user);
      case ALL -> findActiveActivity(user, activeGeneration).or(() -> findRecentActivity(user));
    };
  }

  private Optional<Activity> findActiveActivity(MeetingUser user, int activeGeneration) {
    return user.activities().stream()
        .filter(activity -> activity.generation() == activeGeneration)
        .filter(activity -> toJoinablePart(activity.part()).isPresent())
        .findFirst();
  }

  private Optional<Activity> findRecentActivity(MeetingUser user) {
    return user.activities().stream()
        .filter(activity -> toJoinablePart(activity.part()).isPresent())
        .max(Comparator.comparingInt(Activity::generation));
  }

  private Optional<MeetingJoinablePart> toJoinablePart(Part part) {
    if (part == null) {
      return Optional.empty();
    }
    return switch (part) {
      case PLAN, PM -> Optional.of(MeetingJoinablePart.PM);
      case DESIGN -> Optional.of(MeetingJoinablePart.DESIGN);
      case IOS -> Optional.of(MeetingJoinablePart.IOS);
      case ANDROID -> Optional.of(MeetingJoinablePart.ANDROID);
      case SERVER, BACKEND -> Optional.of(MeetingJoinablePart.SERVER);
      case WEB, FRONTEND -> Optional.of(MeetingJoinablePart.WEB);
      default -> Optional.empty();
    };
  }

  private void validateGeneralCategory(AdvertisementCategory category) {
    if (category == null || !category.isGeneralAdvertisement()) {
      throw new AdvertisementException(INVALID_ADVERTISEMENT_CATEGORY);
    }
  }

  private void validateUpdate(Advertisement advertisement, Advertisement.UpdateValues values) {
    if (advertisement.category() != MEETING_TOP) {
      throw new AdvertisementException(INVALID_MEETING_TOP_ADVERTISEMENT);
    }
    if (values == null || !values.hasUpdateField()) {
      throw new AdvertisementException(INVALID_ADVERTISEMENT_UPDATE);
    }
    validateText(values.desktopImageUrl());
    validateText(values.mobileImageUrl());
    validateText(values.calendarImageUrl());
    validateText(values.titlePrefix());
    validateText(values.titleHighlight());
    validateText(values.titleSuffix());
    validateText(values.subTitle());
  }

  private void validateText(String value) {
    if (value != null && value.isBlank()) {
      throw new AdvertisementException(INVALID_ADVERTISEMENT_VALUE);
    }
  }

  private void validatePeriod(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
      throw new AdvertisementException(INVALID_ADVERTISEMENT_PERIOD);
    }
  }

  private void validateImage(AdvertisementImageStoragePort.UploadImage image) {
    if (image == null
        || image.inputStream() == null
        || image.originalFilename() == null
        || image.originalFilename().isBlank()
        || image.size() <= 0
        || image.contentType() == null
        || !IMAGE_CONTENT_TYPES.contains(image.contentType().toLowerCase())) {
      throw new AdvertisementException(INVALID_ADVERTISEMENT_IMAGE);
    }
  }
}
