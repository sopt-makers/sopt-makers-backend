package org.sopt.makers.api.controller.admin.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminSoptampSuccessCode implements SuccessCode {
  SUCCESS_CLEAR_SOPTAMP_DATA(200, "솝탬프 데이터 초기화가 완료되었습니다"),
  SUCCESS_INIT_POINTS(200, "솝탬프 점수 초기화가 완료되었습니다"),
  SUCCESS_INIT_RANK_CACHE(200, "솝탬프 랭킹 캐시 재적재가 완료되었습니다"),
  SUCCESS_UPDATE_UPSERT_SCHEDULE(200, "솝탬프 upsert 배치 스케줄 변경이 완료되었습니다"),
  SUCCESS_SYNC_RANK_CACHE(200, "솝탬프 랭킹 캐시 동기화가 완료되었습니다"),
  SUCCESS_UPSERT_SOPTAMP_USERS(200, "솝탬프 유저 upsert 배치 실행이 완료되었습니다"),
  SUCCESS_SEND_SOPTAMP_SHOWCASE(200, "솝탬프 쇼케이스 알림 발송이 완료되었습니다");

  private final int statusCode;
  private final String message;
}
