package org.sopt.makers.api.controller.admin.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 솝탬프", description = "어드민 솝탬프 운영 API. 쿼리 파라미터 password 로 인증한다")
public interface AdminSoptampApi {

  @Operation(
      summary = "솝탬프 데이터 초기화",
      description = "쿼리 파라미터로 선택 삭제한다. 스탬프를 지우면 해당 이미지와 박수 데이터도 함께 지워진다.")
  ResponseEntity<BaseResponse<?>> clearSoptampData(
      @Parameter(description = "어드민 비밀번호", required = true) String password,
      @Parameter(description = "스탬프와 박수까지 지울지 여부") boolean stamp,
      @Parameter(description = "솝탬프 유저까지 지울지 여부") boolean soptampUser);

  @Operation(summary = "솝탬프 점수 전체 초기화", description = "전 기수 점수를 0 으로 되돌리고 현재 기수 랭킹 캐시를 다시 채운다.")
  ResponseEntity<BaseResponse<?>> initPoints(
      @Parameter(description = "어드민 비밀번호", required = true) String password);

  @Operation(summary = "솝탬프 랭킹 캐시 재적재", description = "앱잼 시즌에는 400 을 반환한다.")
  ResponseEntity<BaseResponse<?>> initRankCache(
      @Parameter(description = "어드민 비밀번호", required = true) String password);

  @Operation(
      summary = "솝탬프 upsert 배치 스케줄 변경",
      description = "재기동 없이 다음 실행부터 반영된다. cron 이 깨져 있으면 400 을 반환한다.")
  ResponseEntity<BaseResponse<?>> updateUpsertSchedule(
      @Parameter(description = "어드민 비밀번호", required = true) String password,
      @Parameter(description = "스프링 cron 표현식", required = true) String cron);
}
