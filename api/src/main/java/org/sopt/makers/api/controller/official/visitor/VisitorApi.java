package org.sopt.makers.api.controller.official.visitor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "방문자", description = "공식 홈페이지 방문자 API")
public interface VisitorApi {

  @Operation(summary = "방문자 카운트 증가")
  ResponseEntity<BaseResponse<?>> visitorCountUp(HttpServletRequest request);

  @Operation(summary = "오늘 방문자 수 조회")
  ResponseEntity<BaseResponse<?>> getTodayVisitor();
}
