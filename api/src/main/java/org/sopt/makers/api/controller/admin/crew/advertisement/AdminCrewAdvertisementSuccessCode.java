package org.sopt.makers.api.controller.admin.crew.advertisement;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminCrewAdvertisementSuccessCode implements SuccessCode {
  UPDATE_MEETING_TOP_ADVERTISEMENT(200, "모임 상단 광고 수정에 성공했습니다."),
  UPLOAD_MEETING_TOP_ADVERTISEMENT_IMAGE(201, "모임 상단 광고 이미지 업로드에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
