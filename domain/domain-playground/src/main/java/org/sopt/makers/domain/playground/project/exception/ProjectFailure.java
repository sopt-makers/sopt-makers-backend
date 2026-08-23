package org.sopt.makers.domain.playground.project.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ProjectFailure implements FailureCode {

    NOT_FOUND_PROJECT(404, "존재하지 않는 프로젝트입니다."),
    UNAUTHORIZED_PROJECT(403, "수정 권한이 없는 유저입니다."),
    EXCEEDED_IMAGE_COUNT(400, "이미지 개수를 초과했습니다.");

    private final int statusCode;
    private final String message;
}
