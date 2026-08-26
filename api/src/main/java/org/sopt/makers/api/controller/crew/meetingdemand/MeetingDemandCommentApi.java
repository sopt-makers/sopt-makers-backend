package org.sopt.makers.api.controller.crew.meetingdemand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.CreateMeetingDemandCommentRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.GetMeetingDemandCommentsRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.UpdateMeetingDemandCommentRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 모임 수요 댓글", description = "CREW 모임 수요 댓글 API")
public interface MeetingDemandCommentApi {

  @Operation(summary = "모임 수요 댓글·대댓글 조회")
  ResponseEntity<BaseResponse<?>> getComments(
      Long meetingDemandId,
      GetMeetingDemandCommentsRequest request,
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 댓글·대댓글 생성")
  ResponseEntity<BaseResponse<?>> createComment(
      Long meetingDemandId,
      CreateMeetingDemandCommentRequest request,
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 댓글 수정")
  ResponseEntity<BaseResponse<?>> updateComment(
      Long commentId,
      UpdateMeetingDemandCommentRequest request,
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 댓글 삭제")
  ResponseEntity<BaseResponse<?>> deleteComment(
      Long commentId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 댓글 좋아요 토글")
  ResponseEntity<BaseResponse<?>> switchCommentLike(
      Long commentId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "모임 수요 댓글 신고")
  ResponseEntity<BaseResponse<?>> reportComment(
      Long commentId, @Parameter(hidden = true) Long userId);
}
