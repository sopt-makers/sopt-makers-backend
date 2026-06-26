package org.sopt.makers.api.controller.official.project;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ProjectSuccessCode implements SuccessCode {
  GET_PROJECTS(200, "프로젝트 목록 조회에 성공했습니다."),
  GET_PROJECT_DETAIL(200, "프로젝트 상세 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
