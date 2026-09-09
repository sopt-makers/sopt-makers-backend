package org.sopt.makers.domain.playground.member.relation.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum UserRelationFailure implements FailureCode {
  NOT_FOUND_USER(404, "존재하지 않는 유저입니다");

  private final int statusCode;
  private final String message;
}
