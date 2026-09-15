package org.sopt.makers.api.controller.crew.meeting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.meeting.dto.ApplyMeetingRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.CreateMeetingRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.GetMeetingsRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.UpdateApplyStatusRequest;
import org.sopt.makers.api.controller.crew.meeting.dto.UpdateMeetingRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 모임", description = "CREW 모임 API")
public interface MeetingApi {

  @Operation(summary = "모임 생성")
  ResponseEntity<BaseResponse<?>> createMeeting(
      CreateMeetingRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수정")
  ResponseEntity<BaseResponse<?>> updateMeeting(
      Long meetingId, UpdateMeetingRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 삭제")
  ResponseEntity<BaseResponse<?>> deleteMeeting(
      Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "일반 모임 신청")
  ResponseEntity<BaseResponse<?>> applyGeneralMeeting(
      ApplyMeetingRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "행사 모임 신청")
  ResponseEntity<BaseResponse<?>> applyEventMeeting(
      ApplyMeetingRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 신청 취소")
  ResponseEntity<BaseResponse<?>> cancelApply(
      Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 신청 상태 변경")
  ResponseEntity<BaseResponse<?>> updateApplyStatus(
      Long meetingId, UpdateApplyStatusRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 상세 조회")
  ResponseEntity<BaseResponse<?>> getMeeting(Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 목록 조회")
  ResponseEntity<BaseResponse<?>> getMeetings(GetMeetingsRequest request);

  @Operation(summary = "내가 만든 모임 목록 조회")
  ResponseEntity<BaseResponse<?>> getMyMeetings(
      GetMeetingsRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "내가 속한 모임 목록 조회")
  ResponseEntity<BaseResponse<?>> getJoinedMeetings(
      GetMeetingsRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 지원자 목록 조회")
  ResponseEntity<BaseResponse<?>> getMeetingApplicants(
      Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 참여자 목록 조회")
  ResponseEntity<BaseResponse<?>> getMeetingParticipants(
      Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 내 같은 파트/기수 멤버 리스트 조회")
  ResponseEntity<BaseResponse<?>> getMeetingPartMembers(
      Long meetingId, @Parameter(hidden = true) Long userId);
}
