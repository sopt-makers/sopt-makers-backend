package org.sopt.makers.api.controller.app.lecture;

import static org.sopt.makers.api.controller.app.lecture.AppLectureSuccessCode.SUCCESS_GET_LECTURE_ROUND;
import static org.sopt.makers.api.controller.app.lecture.AppLectureSuccessCode.SUCCESS_SINGLE_GET_LECTURE;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.lecture.dto.LectureCurrentRoundResponse;
import org.sopt.makers.api.controller.app.lecture.dto.TodayLectureResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.admin.app.AppLectureResult;
import org.sopt.makers.domain.admin.app.AppSubLecture;
import org.sopt.makers.domain.admin.app.service.AppLectureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/lectures")
public class AppLectureController implements AppLectureApi {

  private final AppLectureService appLectureService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getTodayLecture(@CurrentUserId Long userId) {
    AppLectureResult response = appLectureService.getTodayLecture(userId);
    return ResponseFactory.success(SUCCESS_SINGLE_GET_LECTURE, TodayLectureResponse.from(response));
  }

  @Override
  @GetMapping("/round/{lectureId}")
  public ResponseEntity<BaseResponse<?>> getRound(@PathVariable Long lectureId) {
    AppSubLecture response = appLectureService.getCurrentLectureRound(lectureId);
    return ResponseFactory.success(
        SUCCESS_GET_LECTURE_ROUND, LectureCurrentRoundResponse.from(response));
  }
}
