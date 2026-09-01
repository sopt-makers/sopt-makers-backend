package org.sopt.makers.domain.crew.meeting.facade;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.flash.service.FlashService;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandOpenedNotificationService;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;
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
public class MeetingFacade {

  private final MeetingService meetingService;
  private final MeetingTagService meetingTagService;
  private final FlashService flashService;
  private final MeetingKeywordNotificationPublisher notificationPublisher;
  private final MeetingDemandService meetingDemandService;
  private final MeetingDemandOpenedNotificationService openedNotificationService;

  @Transactional
  public CreatedMeeting createMeeting(CreateMeetingCommand command, Long userId) {
    Long meetingDemandId = command.meetingCommand().meetingDemandId();
    meetingDemandService.validateExists(meetingDemandId);
    Meeting meeting = meetingService.createMeeting(command.meetingCommand(), userId);
    if (meetingDemandId != null) {
      meetingDemandService.open(meetingDemandId);
    }
    MeetingTag meetingTag =
        meetingTagService.createGeneralMeetingTag(
            meeting.id(), command.welcomeMessageTypes(), command.meetingKeywordTypes());
    openedNotificationService.register(meeting);
    notificationPublisher.publish(meeting, command.meetingKeywordTypes());
    return new CreatedMeeting(meeting, meetingTag.id());
  }

  @Transactional
  public MeetingDetailResult updateMeeting(
      Long meetingId, UpdateMeetingCommand command, Long userId) {
    Meeting meeting = meetingService.updateMeeting(meetingId, command.meetingCommand(), userId);
    if (command.welcomeMessageTypes() != null || command.meetingKeywordTypes() != null) {
      meetingTagService.updateGeneralMeetingTag(
          meetingId, command.welcomeMessageTypes(), command.meetingKeywordTypes());
    }
    return getMeetingDetail(meeting.id(), userId);
  }

  @Transactional
  public void deleteMeeting(Long meetingId, Long userId) {
    meetingTagService.deleteByMeetingId(meetingId);
    flashService.deleteByMeetingIdIfExists(meetingId);
    meetingService.deleteMeeting(meetingId, userId);
  }

  public MeetingDetailResult getMeetingDetail(Long meetingId, Long userId) {
    return new MeetingDetailResult(
        meetingService.getMeetingDetail(meetingId, userId),
        meetingTagService.getByMeetingId(meetingId));
  }

  public PageResult<MeetingSummaryResult> searchMeetings(
      MeetingService.SearchMeetingsCommand command, int pageNo, int limit) {
    PageResult<MeetingService.MeetingSummary> meetings =
        meetingService.searchMeetings(command, pageNo, limit);
    Map<Long, MeetingTagService.MeetingTagInfo> tags = getTags(meetings);
    return meetings.map(
        meeting ->
            new MeetingSummaryResult(
                meeting,
                tags.getOrDefault(
                    meeting.meeting().id(), MeetingTagService.MeetingTagInfo.empty())));
  }

  public PageResult<MeetingSummaryResult> findMeetingsByCreator(
      Long userId, int pageNo, int limit) {
    PageResult<MeetingService.MeetingSummary> meetings =
        meetingService.findMeetingsByCreator(userId, pageNo, limit);
    Map<Long, MeetingTagService.MeetingTagInfo> tags = getTags(meetings);
    return meetings.map(
        meeting ->
            new MeetingSummaryResult(
                meeting,
                tags.getOrDefault(
                    meeting.meeting().id(), MeetingTagService.MeetingTagInfo.empty())));
  }

  public PageResult<JoinedMeetingResult> findJoinedMeetings(Long userId, int pageNo, int limit) {
    PageResult<MeetingService.JoinedMeeting> meetings =
        meetingService.findJoinedMeetings(userId, pageNo, limit);
    Map<Long, MeetingTagService.MeetingTagInfo> tags =
        meetingTagService.getByMeetingIds(
            meetings.content().stream().map(joined -> joined.summary().meeting().id()).toList());
    return meetings.map(
        joined ->
            new JoinedMeetingResult(
                joined,
                tags.getOrDefault(
                    joined.summary().meeting().id(), MeetingTagService.MeetingTagInfo.empty())));
  }

  private Map<Long, MeetingTagService.MeetingTagInfo> getTags(
      PageResult<MeetingService.MeetingSummary> meetings) {
    return meetingTagService.getByMeetingIds(
        meetings.content().stream().map(summary -> summary.meeting().id()).toList());
  }

  public record CreateMeetingCommand(
      MeetingService.CreateMeetingCommand meetingCommand,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {}

  public record UpdateMeetingCommand(
      MeetingService.UpdateMeetingCommand meetingCommand,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {}

  public record CreatedMeeting(Meeting meeting, Long tagId) {}

  public record MeetingDetailResult(
      MeetingService.MeetingDetail meetingDetail,
      MeetingTagService.MeetingTagInfo meetingTagInfo) {}

  public record MeetingSummaryResult(
      MeetingService.MeetingSummary meetingSummary,
      MeetingTagService.MeetingTagInfo meetingTagInfo) {}

  public record JoinedMeetingResult(
      MeetingService.JoinedMeeting joinedMeeting,
      MeetingTagService.MeetingTagInfo meetingTagInfo) {}
}
