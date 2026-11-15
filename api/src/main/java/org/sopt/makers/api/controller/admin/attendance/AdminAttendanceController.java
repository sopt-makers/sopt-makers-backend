package org.sopt.makers.api.controller.admin.attendance;

import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_GET_ATTENDANCES_BY_LECTURE;
import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_GET_ATTENDANCES_BY_USER;
import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_UPDATE_ATTENDANCE_SCORE;
import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_UPDATE_SUB_ATTENDANCE;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.attendance.dto.AttendancesByLectureResponse;
import org.sopt.makers.api.controller.admin.attendance.dto.AttendancesByUserResponse;
import org.sopt.makers.api.controller.admin.attendance.dto.SubAttendanceUpdateRequest;
import org.sopt.makers.api.controller.admin.attendance.dto.SubAttendanceUpdateResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.LectureAttendanceDetail;
import org.sopt.makers.domain.admin.attendance.UserAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendances")
public class AdminAttendanceController implements AdminAttendanceApi {

  private final AttendanceService attendanceService;

  @Override
  @GetMapping("/{memberId}")
  public ResponseEntity<BaseResponse<?>> getAttendancesByUser(
      @PathVariable("memberId") Long userId) {
    UserAttendanceSummary attendances = attendanceService.getAttendancesByUserId(userId);
    return ResponseFactory.success(
        SUCCESS_GET_ATTENDANCES_BY_USER, AttendancesByUserResponse.from(attendances));
  }

  @Override
  @GetMapping("/lecture/{lectureId}")
  public ResponseEntity<BaseResponse<?>> getAttendancesByLecture(
      @PathVariable Long lectureId, @RequestParam(required = false) Part part, Pageable pageable) {
    int totalCount = attendanceService.countAttendancesByLecture(lectureId, part);
    List<LectureAttendanceDetail> attendances =
        attendanceService.getAttendancesByLectureId(
            lectureId, part, pageable.getPageNumber(), pageable.getPageSize());
    return ResponseFactory.success(
        SUCCESS_GET_ATTENDANCES_BY_LECTURE,
        AttendancesByLectureResponse.from(totalCount, attendances));
  }

  @Override
  @PatchMapping
  public ResponseEntity<BaseResponse<?>> updateSubAttendance(
      @RequestBody @Valid SubAttendanceUpdateRequest request) {
    var updated =
        attendanceService.updateSubAttendance(request.subAttendanceId(), request.status());
    return ResponseFactory.success(
        SUCCESS_UPDATE_SUB_ATTENDANCE, SubAttendanceUpdateResponse.from(updated));
  }

  @Override
  @PatchMapping("/member/{memberId}")
  public ResponseEntity<BaseResponse<?>> updateAttendanceScore(
      @PathVariable("memberId") Long userId) {
    float score = attendanceService.updateAttendanceScore(userId);
    return ResponseFactory.success(SUCCESS_UPDATE_ATTENDANCE_SCORE, score);
  }
}
