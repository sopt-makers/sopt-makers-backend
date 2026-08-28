package org.sopt.makers.domain.playground.coffeechat.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CoffeeChatFailure implements FailureCode {

  COFFEE_CHAT_NOT_FOUND(404, "커피챗 정보를 확인할 수 없는 유저입니다."),
  COFFEE_CHAT_NOT_REGISTERED(404, "커피챗 정보를 등록한 적 없는 유저입니다."),
  ALREADY_EXISTS_COFFEE_CHAT(400, "이미 커피챗 정보가 등록된 유저입니다."),
  NOT_PARTICIPATED_COFFEE_CHAT(400, "해당 커피챗을 신청한 적 없는 유저입니다."),
  ALREADY_REVIEWED_COFFEE_CHAT(400, "이미 리뷰를 등록한 커피챗입니다."),
  ANONYMOUS_PROFILE_IMAGE_NOT_FOUND(500, "익명 프로필 이미지를 조회할 수 없습니다.");

  private final int statusCode;
  private final String message;
}
