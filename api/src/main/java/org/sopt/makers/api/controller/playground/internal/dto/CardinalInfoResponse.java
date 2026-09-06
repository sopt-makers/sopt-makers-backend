package org.sopt.makers.api.controller.playground.internal.dto;

/** 레거시 InternalOpenApiController와의 JSON 계약(활동 기수/파트 문자열)을 그대로 보존한다. */
public record CardinalInfoResponse(String cardinalInfo) {}
