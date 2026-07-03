package org.sopt.makers.api.controller.official.homepage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "공식 홈페이지", description = "공식 홈페이지 페이지별 조회 API")
public interface HomepageApi {

  @Operation(summary = "메인 페이지 조회", description = "메인 페이지 데이터를 조회합니다.")
  ResponseEntity<BaseResponse<?>> getMainPage();

  @Operation(summary = "About 페이지 조회", description = "About 페이지 데이터를 조회합니다.")
  ResponseEntity<BaseResponse<?>> getAboutPage();

  @Operation(summary = "Recruiting 페이지 조회", description = "Recruiting 페이지 데이터를 조회합니다.")
  ResponseEntity<BaseResponse<?>> getRecruitPage();
}
