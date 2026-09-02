package org.sopt.makers.domain.app.soptamp.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptampFailure implements FailureCode {
  NOT_FOUND_STAMP(400, "존재하지 않는 스탬프입니다."),
  STAMP_DELETE_FORBIDDEN(403, "자신의 스탬프만 삭제할 수 있습니다."),
  DUPLICATE_STAMP(409, "이미 해당 미션에 대한 스탬프가 존재합니다."),
  INVALID_STAMP_CONTENTS(400, "스탬프 내용이 존재하지 않습니다."),
  INVALID_STAMP_IMAGES(400, "스탬프 이미지가 존재하지 않습니다."),
  INVALID_STAMP_ACTIVITY_DATE(400, "스탬프 활동 날짜가 존재하지 않습니다."),
  INVALID_STAMP_MISSION_ID(400, "스탬프 미션 ID가 존재하지 않습니다."),
  NOT_FOUND_MISSION(404, "존재하지 않는 미션입니다."),
  NOT_FOUND_SOPTAMP_USER(404, "존재하지 않는 유저입니다."),
  TEAM_NOT_FOUND(404, "존재하지 않는 팀입니다."),
  TEAM_FORBIDDEN(403, "해당 팀에 대한 권한이 없습니다.");

  private final int statusCode;
  private final String message;
}
