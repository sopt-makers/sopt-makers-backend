package org.sopt.makers.api.controller.crew.meeting;

import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.APPLY_MEETING;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.CANCEL_APPLY_MEETING;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.CREATE_MEETING;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.DELETE_MEETING;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.GET_JOINED_MEETINGS;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.GET_MEETING;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.GET_MEETINGS;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.GET_MEETING_APPLICANTS;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.GET_MEETING_MEMBERS;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.GET_MEETING_PARTICIPANTS;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.UPDATE_APPLY_STATUS;
import static org.sopt.makers.api.controller.crew.meeting.MeetingSuccessCode.UPDATE_MEETING;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.meeting.dto.ApplyMeetingRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.ApplyMeetingResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.CreateMeetingRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.CreateMeetingResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.GetMeetingsRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.JoinedMeetingPageResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingApplicantListResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingApplyResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingDetailResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingPartMembersResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingParticipantListResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingSummaryPageResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.UpdateApplyStatusRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.UpdateMeetingRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meeting/v2")
@RequiredArgsConstructor
public class MeetingController implements MeetingApi {

  private final MeetingService meetingService;
  private final MeetingFacade meetingFacade;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createMeeting(
      @Valid @RequestBody CreateMeetingRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        CREATE_MEETING,
        CreateMeetingResponse.from(meetingFacade.createMeeting(request.toCommand(), userId)));
  }

  @Override
  @PatchMapping("/{meetingId}")
  public ResponseEntity<BaseResponse<?>> updateMeeting(
      @PathVariable Long meetingId,
      @Valid @RequestBody UpdateMeetingRequest request,
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        UPDATE_MEETING,
        MeetingDetailResponse.from(
            meetingFacade.updateMeeting(meetingId, request.toCommand(), userId)));
  }

  @Override
  @DeleteMapping("/{meetingId}")
  public ResponseEntity<BaseResponse<?>> deleteMeeting(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    meetingFacade.deleteMeeting(meetingId, userId);
    return ResponseFactory.success(DELETE_MEETING);
  }

  @Override
  @PostMapping("/apply")
  public ResponseEntity<BaseResponse<?>> applyGeneralMeeting(
      @Valid @RequestBody ApplyMeetingRequest request, @CurrentUserId Long userId) {
    MeetingApply apply = meetingService.applyGeneralMeeting(request.toCommand(), userId);
    return ResponseFactory.success(APPLY_MEETING, ApplyMeetingResponse.from(apply));
  }

  @Override
  @PostMapping("/event/apply")
  public ResponseEntity<BaseResponse<?>> applyEventMeeting(
      @Valid @RequestBody ApplyMeetingRequest request, @CurrentUserId Long userId) {
    MeetingApply apply = meetingService.applyEventMeeting(request.toCommand(), userId);
    return ResponseFactory.success(APPLY_MEETING, ApplyMeetingResponse.from(apply));
  }

  @Override
  @DeleteMapping("/{meetingId}/apply")
  public ResponseEntity<BaseResponse<?>> cancelApply(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    meetingService.cancelApply(meetingId, userId);
    return ResponseFactory.success(CANCEL_APPLY_MEETING);
  }

  @Override
  @PutMapping("/{meetingId}/apply/status")
  public ResponseEntity<BaseResponse<?>> updateApplyStatus(
      @PathVariable Long meetingId,
      @Valid @RequestBody UpdateApplyStatusRequest request,
      @CurrentUserId Long userId) {
    MeetingApply apply = meetingService.updateApplyStatus(meetingId, request.toCommand(), userId);
    return ResponseFactory.success(UPDATE_APPLY_STATUS, MeetingApplyResponse.from(apply));
  }

  @Override
  @GetMapping("/{meetingId}")
  public ResponseEntity<BaseResponse<?>> getMeeting(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING, MeetingDetailResponse.from(meetingFacade.getMeetingDetail(meetingId, userId)));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getMeetings(
      @Valid @ModelAttribute GetMeetingsRequest request) {
    return ResponseFactory.success(
        GET_MEETINGS,
        MeetingSummaryPageResponse.from(
            meetingFacade.searchMeetings(
                request.toCommand(), request.pageNoOrDefault(), request.limitOrDefault())));
  }

  @Override
  @GetMapping("/me")
  public ResponseEntity<BaseResponse<?>> getMyMeetings(
      @Valid @ModelAttribute GetMeetingsRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETINGS,
        MeetingSummaryPageResponse.from(
            meetingFacade.findMeetingsByCreator(
                userId, request.pageNoOrDefault(), request.limitOrDefault())));
  }

  @Override
  @GetMapping("/me/joined")
  public ResponseEntity<BaseResponse<?>> getJoinedMeetings(
      @Valid @ModelAttribute GetMeetingsRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_JOINED_MEETINGS,
        JoinedMeetingPageResponse.from(
            meetingFacade.findJoinedMeetings(
                userId, request.pageNoOrDefault(), request.limitOrDefault())));
  }

  @Override
  @GetMapping("/{meetingId}/applicants")
  public ResponseEntity<BaseResponse<?>> getMeetingApplicants(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING_APPLICANTS,
        MeetingApplicantListResponse.from(meetingService.getApplicants(meetingId, userId)));
  }

  @Override
  @GetMapping("/{meetingId}/participants")
  public ResponseEntity<BaseResponse<?>> getMeetingParticipants(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING_PARTICIPANTS,
        MeetingParticipantListResponse.from(meetingService.getParticipants(meetingId, userId)));
  }

  @Override
  @GetMapping("/{meetingId}/members")
  public ResponseEntity<BaseResponse<?>> getMeetingPartMembers(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING_MEMBERS,
        MeetingPartMembersResponse.from(meetingService.getMeetingPartMembers(meetingId, userId)));
  }
}
