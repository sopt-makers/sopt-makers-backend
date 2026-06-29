package org.sopt.makers.api.controller.official.recruit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "리크루팅", description = "공식 홈페이지 리크루팅 API")
public interface RecruitPageApi {

  @Operation(summary = "지원서 메인 페이지 조회", description = "지원서 메인 페이지 데이터를 조회합니다.")
  ResponseEntity<BaseResponse<?>> getRecruitMainPage();

  @Operation(summary = "지원서 파트 상세 조회", description = "파트별 소개, 선호하는 인재상, 커리큘럼을 조회합니다.")
  ResponseEntity<BaseResponse<?>> getPartDetail(String part);
}
