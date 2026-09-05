package org.sopt.makers.api.controller.admin.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 스케줄", description = "스케줄 수동 실행 API. 액세스 토큰과 x-admin-password 헤더 두 겹으로 인증한다")
@SecurityRequirement(name = "Authorization")
public interface AdminScheduleApi {

  @Operation(summary = "솝탬프 랭킹 캐시 수동 동기화", description = "앱잼 시즌에는 아무것도 하지 않는다.")
  ResponseEntity<BaseResponse<?>> syncSoptampRankCache(
      @Parameter(description = "어드민 비밀번호", required = true) String password);

  @Operation(
      summary = "솝탬프 유저 upsert 배치 수동 실행",
      description =
          "요청 스레드에서 동기로 돌기 때문에 완료될 때까지 응답이 나가지 않는다. 건너뛰거나 누락된 유저가 있어도 200 으로 끝나고, 집계와 실패 여부는 응답 본문에 담긴다.")
  ResponseEntity<BaseResponse<?>> upsertSoptampUsers(
      @Parameter(description = "어드민 비밀번호", required = true) String password);
}
