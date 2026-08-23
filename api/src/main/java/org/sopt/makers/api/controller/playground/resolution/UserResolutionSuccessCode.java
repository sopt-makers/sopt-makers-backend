package org.sopt.makers.api.controller.playground.resolution;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum UserResolutionSuccessCode implements SuccessCode {

    GET_RESOLUTION(200, "다짐 메시지 조회 성공"),
    CREATE_RESOLUTION(201, "다짐 메시지 생성 성공"),
    VALIDATE_RESOLUTION(200, "다짐 메시지 유효성 검사 성공"),
    DELETE_RESOLUTION(204, "다짐 메시지 삭제 성공"),
    GET_LUCKY_PICK_RESULT(200, "행운 뽑기 결과 조회 성공");

    private final int statusCode;
    private final String message;
}
