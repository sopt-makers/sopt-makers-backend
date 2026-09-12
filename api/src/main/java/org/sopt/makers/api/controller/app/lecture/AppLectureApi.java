package org.sopt.makers.api.controller.app.lecture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.lecture.dto.LectureCurrentRoundResponse;
import org.sopt.makers.api.controller.app.lecture.dto.TodayLectureResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 세션", description = "앱 세션 API")
public interface AppLectureApi {

  @Operation(summary = "진행 중인 세션 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "진행 중인 세션 조회가 완료되었습니다"),
    @ApiResponse(responseCode = "400", description = "세션 개수가 올바르지 않습니다", content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "현재 기수 출석 정보를 찾을 수 없거나, 세부 출석 정보를 찾을 수 없습니다",
        content = @Content)
  })
  ResponseEntity<BaseResponse<TodayLectureResponse>> getTodayLecture(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "출석 차수 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "출석 차수 조회가 완료되었습니다"),
    @ApiResponse(
        responseCode = "400",
        description = "세션이 아직 시작되지 않았거나, 출석 체크 시간이 종료되었거나, 세션이 종료되었습니다",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 세션이거나, 세부 출석 정보를 찾을 수 없습니다",
        content = @Content)
  })
  ResponseEntity<BaseResponse<LectureCurrentRoundResponse>> getRound(
      @Parameter(description = "세션 아이디", example = "1") Long lectureId);
}
