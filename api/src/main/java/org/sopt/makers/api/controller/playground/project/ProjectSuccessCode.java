package org.sopt.makers.api.controller.playground.project;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ProjectSuccessCode implements SuccessCode {
  GET_RANDOM_PROJECTS(200, "랜덤 프로젝트 조회 성공"),
  GET_PROJECT(200, "프로젝트 조회 성공"),
  GET_PROJECTS(200, "프로젝트 목록 조회 성공"),
  CREATE_PROJECT(201, "프로젝트 생성 성공"),
  UPDATE_PROJECT(200, "프로젝트 수정 성공"),
  DELETE_PROJECT(200, "프로젝트 삭제 성공");

  private final int statusCode;
  private final String message;
}
