package org.sopt.makers.api.controller.crew.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.notice.service.NoticeService;

public record CreateNoticeRequest(
    @NotBlank String title,
    @NotBlank String subTitle,
    @NotBlank String contents,
    @NotNull LocalDateTime exposeStartDate,
    @NotNull LocalDateTime exposeEndDate,
    @NotBlank String noticeSecretKey) {

  public NoticeService.CreateNoticeCommand toCommand() {
    return new NoticeService.CreateNoticeCommand(
        title, subTitle, contents, exposeStartDate, exposeEndDate, noticeSecretKey);
  }
}
