package org.sopt.makers.api.controller.app.schedule;

import static org.sopt.makers.api.controller.app.schedule.AppScheduleSuccessCode.SUCCESS_GET_SCHEDULES;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.app.schedule.dto.ScheduleListResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.app.service.AppScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/schedules")
public class AppScheduleController implements AppScheduleApi {

  private final AppScheduleService appScheduleService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getSchedules(
      @RequestParam @DateTimeFormat(iso = DATE_TIME) LocalDateTime start,
      @RequestParam @DateTimeFormat(iso = DATE_TIME) LocalDateTime end) {
    Map<LocalDate, List<AppSchedule>> response = appScheduleService.getSchedules(start, end);
    return ResponseFactory.success(SUCCESS_GET_SCHEDULES, ScheduleListResponse.from(response));
  }
}
