package org.sopt.makers.domain.crew.flash.facade;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.flash.Flash;
import org.sopt.makers.domain.crew.flash.FlashPlaceType;
import org.sopt.makers.domain.crew.flash.FlashTimingType;
import org.sopt.makers.domain.crew.flash.service.FlashService;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTag;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;
import org.sopt.makers.domain.crew.meeting.tag.service.MeetingTagService;
import org.sopt.makers.domain.crew.notification.service.MeetingKeywordNotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlashFacade {

  private static final String DEFAULT_IMAGE_URL =
      "https://makers-web-img.s3.ap-northeast-2.amazonaws.com/flash/flash.png";

  private final MeetingService meetingService;
  private final FlashService flashService;
  private final MeetingTagService meetingTagService;
  private final MeetingKeywordNotificationPublisher notificationPublisher;
  private final Clock clock;

  @Transactional
  public CreatedFlash createFlash(CreateFlashCommand command, Long userId) {
    FlashCommandValues values =
        command.values().withImages(resolveImages(command.values().images()));

    Meeting meeting =
        meetingService.createFlashMeeting(
            new MeetingService.CreateFlashMeetingCommand(
                values.title(),
                values.description(),
                values.activityStartDate(),
                values.activityEndDate(),
                values.maximumCapacity(),
                values.images()),
            userId);

    Flash flash =
        flashService.createFlash(
            userId, meeting.id(), values.toDomainValues(meeting), meeting.createdGeneration());
    MeetingTag meetingTag =
        meetingTagService.createFlashTag(
            flash.id(), meeting.id(), command.welcomeMessageTypes(), command.meetingKeywordTypes());
    notificationPublisher.publish(meeting, command.meetingKeywordTypes());
    return new CreatedFlash(meeting.id(), meetingTag.id());
  }

  public FlashDetail getFlashDetail(Long meetingId, Long userId) {
    Flash flash = flashService.getByMeetingId(meetingId);
    return new FlashDetail(
        flash,
        meetingService.getMeetingDetail(meetingId, userId),
        meetingTagService.getByFlashId(flash.id()),
        flash.getStatus(LocalDateTime.now(clock)));
  }

  @Transactional
  public FlashDetail updateFlash(Long meetingId, UpdateFlashCommand command, Long userId) {
    FlashCommandValues values =
        command.values().withImages(resolveImages(command.values().images()));

    Meeting meeting =
        meetingService.updateFlashMeeting(
            meetingId,
            new MeetingService.UpdateFlashMeetingCommand(
                values.title(),
                values.description(),
                values.activityStartDate(),
                values.activityEndDate(),
                values.maximumCapacity(),
                values.images()),
            userId);

    Flash flash = flashService.updateFlash(meetingId, userId, values.toDomainValues(meeting));
    meetingTagService.updateFlashTag(
        flash.id(), command.welcomeMessageTypes(), command.meetingKeywordTypes());
    return getFlashDetail(meetingId, userId);
  }

  private List<MeetingImage> resolveImages(List<MeetingImage> images) {
    if (images != null && !images.isEmpty()) {
      return images;
    }
    return List.of(new MeetingImage(0, DEFAULT_IMAGE_URL));
  }

  public record CreateFlashCommand(
      FlashCommandValues values,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {}

  public record UpdateFlashCommand(
      FlashCommandValues values,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {}

  public record FlashCommandValues(
      String title,
      String description,
      FlashTimingType timingType,
      LocalDateTime activityStartDate,
      LocalDateTime activityEndDate,
      FlashPlaceType placeType,
      String place,
      Integer minimumCapacity,
      Integer maximumCapacity,
      List<MeetingImage> images) {

    public FlashCommandValues {
      images = images == null ? List.of() : List.copyOf(images);
    }

    public FlashCommandValues withImages(List<MeetingImage> resolvedImages) {
      return new FlashCommandValues(
          title,
          description,
          timingType,
          activityStartDate,
          activityEndDate,
          placeType,
          place,
          minimumCapacity,
          maximumCapacity,
          resolvedImages);
    }

    public Flash.UpdateValues toDomainValues(Meeting meeting) {
      return new Flash.UpdateValues(
          title,
          description,
          timingType,
          meeting.startDate(),
          meeting.endDate(),
          activityStartDate,
          activityEndDate,
          placeType,
          place,
          minimumCapacity,
          maximumCapacity,
          images);
    }
  }

  public record CreatedFlash(Long meetingId, Long tagId) {}

  public record FlashDetail(
      Flash flash,
      MeetingService.MeetingDetail meetingDetail,
      MeetingTagService.MeetingTagInfo meetingTagInfo,
      MeetingStatus status) {}
}
