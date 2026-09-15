package org.sopt.makers.api.controller.app.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.member.dto.AttendanceTotalResponse;
import org.sopt.makers.api.controller.app.member.dto.MemberScoreResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 멤버", description = "앱 멤버 API")
public interface AppMemberApi {

  @Operation(summary = "전체 출석 정보 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "전체 출석 정보 조회가 완료되었습니다"),
    @ApiResponse(responseCode = "404", description = "현재 기수 출석 정보를 찾을 수 없습니다", content = @Content)
  })
  ResponseEntity<BaseResponse<AttendanceTotalResponse>> getMemberTotalAttendance(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "출석 점수 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "출석 점수 조회가 완료되었습니다"),
    @ApiResponse(responseCode = "404", description = "현재 기수 출석 정보를 찾을 수 없습니다", content = @Content)
  })
  ResponseEntity<BaseResponse<MemberScoreResponse>> getScore(@Parameter(hidden = true) Long userId);
}
