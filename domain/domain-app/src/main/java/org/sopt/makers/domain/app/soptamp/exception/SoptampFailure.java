package org.sopt.makers.domain.app.soptamp.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SoptampFailure implements FailureCode {
  INVALID_CLAP_COUNT(400, "잘못된 박수 횟수입니다."),
  INVALID_APPJAM_SEASON_REQUEST(400, "앱잼탬프 시즌이므로 부적절한 요청입니다."),
  INVALID_UPSERT_CRON(400, "잘못된 파라미터 입니다."),
  NOT_FOUND_STAMP(400, "존재하지 않는 스탬프입니다."),
  UNSUPPORTED_RANKING_PART(400, "솝탬프 랭킹을 제공하지 않는 파트입니다."),
  INVALID_STAMP_CONTENTS(400, "스탬프 내용이 존재하지 않습니다."),
  INVALID_STAMP_IMAGES(400, "스탬프 이미지가 존재하지 않습니다."),
  INVALID_STAMP_ACTIVITY_DATE(400, "스탬프 활동 날짜가 존재하지 않습니다."),
  INVALID_STAMP_MISSION_ID(400, "스탬프 미션 ID가 존재하지 않습니다."),
  INVALID_APP_ADMIN_PASSWORD(401, "잘못된 앱 어드민 패스워드입니다."),
  FORBIDDEN_SELF_CLAP(403, "타인의 스탬프에만 박수 칠 수 있습니다."),
  FORBIDDEN_CLAP_LIST(403, "내 미션에서만 박수 목록을 조회할 수 있습니다."),
  STAMP_DELETE_FORBIDDEN(403, "자신의 스탬프만 삭제할 수 있습니다."),
  TEAM_FORBIDDEN(403, "해당 팀에 대한 권한이 없습니다."),
  NOT_FOUND_SOPTAMP_USER(404, "존재하지 않는 유저입니다."),
  NOT_FOUND_MISSION(404, "존재하지 않는 미션입니다."),
  NOT_FOUND_USER_PART(404, "파트 정보를 찾을 수 없습니다."),
  TEAM_NOT_FOUND(404, "존재하지 않는 팀입니다."),
  NICKNAME_IS_FULL(409, "사용 가능한 닉네임이 없습니다."),
  DUPLICATE_STAMP(409, "이미 해당 미션에 대한 스탬프가 존재합니다.");

  private final int statusCode;
  private final String message;
}
