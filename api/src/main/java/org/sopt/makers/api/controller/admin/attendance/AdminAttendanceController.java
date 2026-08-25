package org.sopt.makers.api.controller.admin.attendance;

import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_GET_ATTENDANCES_BY_LECTURE;
import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_GET_ATTENDANCES_BY_USER;
import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_UPDATE_ATTENDANCE_SCORE;
import static org.sopt.makers.api.controller.admin.attendance.AdminAttendanceSuccessCode.SUCCESS_UPDATE_SUB_ATTENDANCE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.attendance.dto.AttendancesByLectureResponse;
import org.sopt.makers.api.controller.admin.attendance.dto.AttendancesByUserResponse;
import org.sopt.makers.api.controller.admin.attendance.dto.SubAttendanceUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
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
@RequestMapping("/api/v1/admin/attendances")
public class AdminAttendanceController implements AdminAttendanceApi {

  private final AttendanceService attendanceService;

  @Override
  @GetMapping("/users/{userId}")
  public ResponseEntity<BaseResponse<?>> getAttendancesByUser(@PathVariable Long userId) {
    List<Attendance> attendances = attendanceService.getAttendancesByUserId(userId);
    return ResponseFactory.success(
        SUCCESS_GET_ATTENDANCES_BY_USER, AttendancesByUserResponse.from(attendances));
  }

  @Override
  @GetMapping("/lectures/{lectureId}")
  public ResponseEntity<BaseResponse<?>> getAttendancesByLecture(
      @PathVariable Long lectureId,
      @RequestParam(required = false) Part part,
      @Min(0) @RequestParam(defaultValue = "0") int page,
      @Min(1) @Max(100) @RequestParam(defaultValue = "20") int limit) {
    int totalCount = attendanceService.countAttendancesByLecture(lectureId, part);
    List<Attendance> attendances =
        attendanceService.getAttendancesByLectureId(lectureId, part, page, limit);
    return ResponseFactory.success(
        SUCCESS_GET_ATTENDANCES_BY_LECTURE,
        AttendancesByLectureResponse.from(totalCount, attendances));
  }

  @Override
  @PatchMapping("/sub-attendances")
  public ResponseEntity<BaseResponse<?>> updateSubAttendance(
      @RequestBody @Valid SubAttendanceUpdateRequest request) {
    attendanceService.updateSubAttendance(request.subAttendanceId(), request.status());
    return ResponseFactory.success(SUCCESS_UPDATE_SUB_ATTENDANCE);
  }

  @Override
  @PatchMapping("/users/{userId}/score")
  public ResponseEntity<BaseResponse<?>> updateAttendanceScore(
      @PathVariable Long userId, @RequestParam int generation) {
    attendanceService.updateAttendanceScore(userId, generation);
    return ResponseFactory.success(SUCCESS_UPDATE_ATTENDANCE_SCORE);
  }
}
