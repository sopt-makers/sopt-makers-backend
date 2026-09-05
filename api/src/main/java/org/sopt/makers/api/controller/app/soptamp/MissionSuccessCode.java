package org.sopt.makers.api.controller.app.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MissionSuccessCode implements SuccessCode {
  GET_ALL_MISSIONS(200, "전체 미션 조회에 성공했습니다."),
  REGISTER_MISSION(200, "미션 생성에 성공했습니다."),
  GET_COMPLETED_MISSIONS(200, "완료한 미션 조회에 성공했습니다."),
  GET_INCOMPLETE_MISSIONS(200, "미완료 미션 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
