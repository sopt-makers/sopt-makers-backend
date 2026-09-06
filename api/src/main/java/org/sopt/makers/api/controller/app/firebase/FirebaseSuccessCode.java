package org.sopt.makers.api.controller.app.firebase;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum FirebaseSuccessCode implements SuccessCode {
  GET_FIREBASE_INFO(200, "firebase 정보 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
