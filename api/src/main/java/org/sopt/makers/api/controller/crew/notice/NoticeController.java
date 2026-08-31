package org.sopt.makers.api.controller.crew.notice;

import static org.sopt.makers.api.controller.crew.notice.NoticeSuccessCode.CREATE_NOTICE;
import static org.sopt.makers.api.controller.crew.notice.NoticeSuccessCode.GET_NOTICES;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.crew.notice.dto.CreateNoticeRequest;
import org.sopt.makers.api.controller.crew.notice.dto.NoticeResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.notice.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice/v2")
@RequiredArgsConstructor
public class NoticeController implements NoticeApi {

  private final NoticeService noticeService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getNotices() {
    return ResponseFactory.success(
        GET_NOTICES, noticeService.getNotices().stream().map(NoticeResponse::from).toList());
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createNotice(
      @Valid @RequestBody CreateNoticeRequest request) {
    noticeService.createNotice(request.toCommand());
    return ResponseFactory.success(CREATE_NOTICE);
  }
}
