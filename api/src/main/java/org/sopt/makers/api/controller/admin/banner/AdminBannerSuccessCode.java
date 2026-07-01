package org.sopt.makers.api.controller.admin.banner;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminBannerSuccessCode implements SuccessCode {
  SUCCESS_GET_BANNER_DETAIL(200, "배너 상세 조회가 완료되었습니다"),
  SUCCESS_GET_BANNER_LIST(200, "배너 목록 조회가 완료되었습니다"),
  SUCCESS_DELETE_BANNER(200, "배너 삭제가 완료되었습니다"),
  SUCCESS_GET_EXTERNAL_BANNERS(200, "게시 중인 외부 배너 리스트 조회가 완료되었습니다"),
  SUCCESS_CREATE_BANNER(201, "배너 생성이 완료되었습니다"),
  SUCCESS_UPDATE_BANNER(200, "배너 수정이 완료되었습니다");

  private final int statusCode;
  private final String message;
}
