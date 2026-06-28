package org.sopt.makers.api.controller.app.member;

import static org.sopt.makers.api.controller.app.member.AppMemberSuccessCode.SUCCESS_GET_ATTENDANCE_SCORE;
import static org.sopt.makers.api.controller.app.member.AppMemberSuccessCode.SUCCESS_GET_TOTAL_ATTENDANCE;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.member.dto.AttendanceTotalResponse;
import org.sopt.makers.api.controller.app.member.dto.MemberScoreResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;
import org.sopt.makers.domain.admin.app.service.AppMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/members")
public class AppMemberController implements AppMemberApi {

  private final AppMemberService appMemberService;

  @Override
  @GetMapping("/attendances")
  public ResponseEntity<BaseResponse<?>> getMemberTotalAttendance(@CurrentUserId Long userId) {
    AppMemberAttendanceSummary response = appMemberService.getMemberTotalAttendance(userId);
    return ResponseFactory.success(
        SUCCESS_GET_TOTAL_ATTENDANCE, AttendanceTotalResponse.from(response));
  }

  @Override
  @GetMapping("/score")
  public ResponseEntity<BaseResponse<?>> getScore(@CurrentUserId Long userId) {
    float response = appMemberService.getMemberScore(userId);
    return ResponseFactory.success(
        SUCCESS_GET_ATTENDANCE_SCORE, MemberScoreResponse.from(response));
  }
}
