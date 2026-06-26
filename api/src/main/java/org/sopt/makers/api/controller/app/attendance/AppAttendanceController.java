package org.sopt.makers.api.controller.app.attendance;

import static org.sopt.makers.api.controller.app.attendance.AppAttendanceSuccessCode.SUCCESS_ATTEND;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.attendance.dto.AttendRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/attendances")
public class AppAttendanceController implements AppAttendanceApi {

  private final AttendanceService attendanceService;

  @Override
  @PostMapping("/attend")
  public ResponseEntity<BaseResponse<?>> attend(
      @CurrentUserId Long userId, @RequestBody @Valid AttendRequest request) {
    attendanceService.attend(userId, request.subLectureId(), request.code());
    return ResponseFactory.success(SUCCESS_ATTEND);
  }
}
