package org.sopt.makers.api.controller.crew.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.notice.dto.CreateNoticeRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 공지", description = "CREW 공지 API")
public interface NoticeApi {

  @Operation(summary = "현재 노출 중인 공지 조회")
  ResponseEntity<BaseResponse<?>> getNotices();

  @Operation(summary = "공지 생성")
  ResponseEntity<BaseResponse<?>> createNotice(CreateNoticeRequest request);
}
