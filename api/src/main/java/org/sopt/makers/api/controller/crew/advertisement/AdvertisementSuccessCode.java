package org.sopt.makers.api.controller.crew.advertisement;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdvertisementSuccessCode implements SuccessCode {
  GET_ADVERTISEMENTS(200, "광고 조회에 성공했습니다."),
  GET_MEETING_TOP_ADVERTISEMENT(200, "모임 상단 광고 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
