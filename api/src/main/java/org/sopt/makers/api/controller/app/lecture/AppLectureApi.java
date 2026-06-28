package org.sopt.makers.api.controller.app.lecture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 세션", description = "앱 세션 API")
public interface AppLectureApi {

  @Operation(summary = "진행 중인 세션 조회")
  ResponseEntity<BaseResponse<?>> getTodayLecture(@Parameter(hidden = true) Long userId);

  @Operation(summary = "출석 차수 조회")
  ResponseEntity<BaseResponse<?>> getRound(Long lectureId);
}
