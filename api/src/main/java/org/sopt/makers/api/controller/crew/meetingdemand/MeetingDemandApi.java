package org.sopt.makers.api.controller.crew.meetingdemand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.CreateMeetingDemandRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.GetMeetingDemandsRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.GetOpenedMeetingsRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 모임 수요", description = "CREW 모임 수요 API")
public interface MeetingDemandApi {

  @Operation(summary = "모임 수요 목록 조회")
  ResponseEntity<BaseResponse<?>> getMeetingDemands(
      GetMeetingDemandsRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 상세 조회")
  ResponseEntity<BaseResponse<?>> getMeetingDemand(
      Long meetingDemandId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 기반 개설 모임 조회")
  ResponseEntity<BaseResponse<?>> getOpenedMeetings(
      Long meetingDemandId, GetOpenedMeetingsRequest request);

  @Operation(summary = "모임 수요 생성")
  ResponseEntity<BaseResponse<?>> createMeetingDemand(
      CreateMeetingDemandRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 삭제")
  ResponseEntity<BaseResponse<?>> deleteMeetingDemand(
      Long meetingDemandId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 기다려요 토글")
  ResponseEntity<BaseResponse<?>> toggleMeetingDemandWait(
      Long meetingDemandId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 신고")
  ResponseEntity<BaseResponse<?>> reportMeetingDemand(
      Long meetingDemandId, @Parameter(hidden = true) Long userId);
}
