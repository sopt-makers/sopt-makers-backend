package org.sopt.makers.api.controller.playground.coffeechat;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CoffeeChatSuccessCode implements SuccessCode {

  SEND_COFFEE_CHAT_REQUEST(200, "커피챗 신청 성공"),
  GET_COFFEE_CHAT_DETAIL(200, "커피챗 상세 조회 성공"),
  GET_COFFEE_CHAT_ACTIVATE(200, "커피챗 활성화 여부 조회 성공"),
  UPDATE_COFFEE_CHAT_OPEN(200, "커피챗 오픈 여부 변경 성공"),
  GET_RECENT_COFFEE_CHAT_LIST(200, "최근 커피챗 목록 조회 성공"),
  GET_SEARCH_COFFEE_CHAT_LIST(200, "커피챗 검색 목록 조회 성공"),
  GET_COFFEE_CHAT_HISTORIES(200, "커피챗 히스토리 조회 성공"),
  CREATE_COFFEE_CHAT_DETAILS(200, "커피챗 정보 등록 성공"),
  UPDATE_COFFEE_CHAT_DETAILS(200, "커피챗 정보 수정 성공"),
  DELETE_COFFEE_CHAT_DETAILS(200, "커피챗 정보 삭제 성공"),
  CREATE_COFFEE_CHAT_REVIEW(200, "커피챗 리뷰 등록 성공"),
  GET_RECENT_COFFEE_CHAT_REVIEWS(200, "최근 커피챗 리뷰 조회 성공"),
  GET_RANDOM_COFFEE_CHAT_LIST(200, "랜덤 커피챗 목록 조회 성공");

  private final int statusCode;
  private final String message;
}
