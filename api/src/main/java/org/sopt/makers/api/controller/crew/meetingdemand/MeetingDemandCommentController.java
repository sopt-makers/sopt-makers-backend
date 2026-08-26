package org.sopt.makers.api.controller.crew.meetingdemand;

import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.CREATE_MEETING_DEMAND_COMMENT;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.DELETE_MEETING_DEMAND_COMMENT;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.GET_MEETING_DEMAND_COMMENTS;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.REPORT_MEETING_DEMAND_COMMENT;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.SWITCH_MEETING_DEMAND_COMMENT_LIKE;
import static org.sopt.makers.api.controller.crew.meetingdemand.MeetingDemandSuccessCode.UPDATE_MEETING_DEMAND_COMMENT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.CreateMeetingDemandCommentRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.CreateMeetingDemandCommentResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.GetMeetingDemandCommentsRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.MeetingDemandCommentPageResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.MeetingDemandReportResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.SwitchMeetingDemandCommentLikeResponse;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.UpdateMeetingDemandCommentRequest;
import org.sopt.makers.api.controller.crew.meetingdemand.dto.UpdateMeetingDemandCommentResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meeting-demand/v2")
@RequiredArgsConstructor
public class MeetingDemandCommentController implements MeetingDemandCommentApi {

  private final MeetingDemandCommentService commentService;

  @Override
  @GetMapping("/{meetingDemandId}/comments")
  public ResponseEntity<BaseResponse<?>> getComments(
      @PathVariable Long meetingDemandId,
      @Valid @ModelAttribute GetMeetingDemandCommentsRequest request,
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING_DEMAND_COMMENTS,
        MeetingDemandCommentPageResponse.from(
            commentService.findComments(
                meetingDemandId, userId, request.pageOrDefault(), request.takeOrDefault())));
  }

  @Override
  @PostMapping("/{meetingDemandId}/comments")
  public ResponseEntity<BaseResponse<?>> createComment(
      @PathVariable Long meetingDemandId,
      @Valid @RequestBody CreateMeetingDemandCommentRequest request,
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        CREATE_MEETING_DEMAND_COMMENT,
        CreateMeetingDemandCommentResponse.from(
            commentService.createComment(meetingDemandId, request.toCommand(), userId)));
  }

  @Override
  @PutMapping("/comments/{commentId}")
  public ResponseEntity<BaseResponse<?>> updateComment(
      @PathVariable Long commentId,
      @Valid @RequestBody UpdateMeetingDemandCommentRequest request,
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        UPDATE_MEETING_DEMAND_COMMENT,
        UpdateMeetingDemandCommentResponse.from(
            commentService.updateComment(commentId, request.contents(), userId)));
  }

  @Override
  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<BaseResponse<?>> deleteComment(
      @PathVariable Long commentId, @CurrentUserId Long userId) {
    commentService.deleteComment(commentId, userId);
    return ResponseFactory.success(DELETE_MEETING_DEMAND_COMMENT);
  }

  @Override
  @PostMapping("/comments/{commentId}/like")
  public ResponseEntity<BaseResponse<?>> switchCommentLike(
      @PathVariable Long commentId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        SWITCH_MEETING_DEMAND_COMMENT_LIKE,
        new SwitchMeetingDemandCommentLikeResponse(
            commentService.switchCommentLike(commentId, userId)));
  }

  @Override
  @PostMapping("/comments/{commentId}/report")
  public ResponseEntity<BaseResponse<?>> reportComment(
      @PathVariable Long commentId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        REPORT_MEETING_DEMAND_COMMENT,
        MeetingDemandReportResponse.from(commentService.reportComment(commentId, userId)));
  }
}
