package org.sopt.makers.api.controller.crew.meetingdemand;

import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.CREATE_MEETING_DEMAND;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.DELETE_MEETING_DEMAND;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.GET_MEETING_DEMAND;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.GET_MEETING_DEMANDS;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.GET_OPENED_MEETINGS;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.REPORT_MEETING_DEMAND;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.TOGGLE_MEETING_DEMAND_WAIT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.CreateMeetingDemandRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.CreateMeetingDemandResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.GetMeetingDemandsRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.GetOpenedMeetingsRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.MeetingDemandDetailResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.MeetingDemandPageResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.MeetingDemandReportResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.OpenedMeetingPageResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.ToggleMeetingDemandWaitResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meeting-demand/v2")
@RequiredArgsConstructor
public class MeetingDemandController implements MeetingDemandApi {

  private final MeetingDemandService meetingDemandService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getMeetingDemands(
      @Valid @ModelAttribute GetMeetingDemandsRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING_DEMANDS,
        MeetingDemandPageResponse.from(
            meetingDemandService.findMeetingDemands(
                userId, request.pageOrDefault(), request.takeOrDefault())));
  }

  @Override
  @GetMapping("/{meetingDemandId}")
  public ResponseEntity<BaseResponse<?>> getMeetingDemand(
      @PathVariable Long meetingDemandId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING_DEMAND,
        MeetingDemandDetailResponse.from(
            meetingDemandService.getMeetingDemand(meetingDemandId, userId)));
  }

  @Override
  @GetMapping("/{meetingDemandId}/meetings")
  public ResponseEntity<BaseResponse<?>> getOpenedMeetings(
      @PathVariable Long meetingDemandId, @Valid @ModelAttribute GetOpenedMeetingsRequest request) {
    return ResponseFactory.success(
        GET_OPENED_MEETINGS,
        OpenedMeetingPageResponse.from(
            meetingDemandService.findOpenedMeetings(
                meetingDemandId, request.pageOrDefault(), request.takeOrDefault())));
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createMeetingDemand(
      @Valid @RequestBody CreateMeetingDemandRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        CREATE_MEETING_DEMAND,
        CreateMeetingDemandResponse.from(
            meetingDemandService.createMeetingDemand(request.toCommand(), userId)));
  }

  @Override
  @DeleteMapping("/{meetingDemandId}")
  public ResponseEntity<BaseResponse<?>> deleteMeetingDemand(
      @PathVariable Long meetingDemandId, @CurrentUserId Long userId) {
    meetingDemandService.deleteMeetingDemand(meetingDemandId, userId);
    return ResponseFactory.success(DELETE_MEETING_DEMAND);
  }

  @Override
  @PostMapping("/{meetingDemandId}/wait")
  public ResponseEntity<BaseResponse<?>> toggleMeetingDemandWait(
      @PathVariable Long meetingDemandId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        TOGGLE_MEETING_DEMAND_WAIT,
        ToggleMeetingDemandWaitResponse.from(
            meetingDemandService.toggleWait(meetingDemandId, userId)));
  }

  @Override
  @PostMapping("/{meetingDemandId}/report")
  public ResponseEntity<BaseResponse<?>> reportMeetingDemand(
      @PathVariable Long meetingDemandId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        REPORT_MEETING_DEMAND,
        MeetingDemandReportResponse.from(
            meetingDemandService.reportMeetingDemand(meetingDemandId, userId)));
  }
}
