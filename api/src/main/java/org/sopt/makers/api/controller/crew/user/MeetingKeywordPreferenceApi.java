package org.sopt.makers.api.controller.crew.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.user.dto.UpdateMeetingKeywordPreferenceRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 관심 키워드", description = "CREW 사용자 관심 모임 키워드 API")
public interface MeetingKeywordPreferenceApi {

  @Operation(summary = "사용자 관심 모임 키워드 설정")
  ResponseEntity<BaseResponse<?>> updateMeetingKeywordPreference(
      UpdateMeetingKeywordPreferenceRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "사용자 관심 모임 키워드 조회")
  ResponseEntity<BaseResponse<?>> getMeetingKeywordPreference(
      @Parameter(hidden = true) Long userId);
}
