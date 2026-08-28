package org.sopt.makers.api.controller.crew.notice.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.notice.Notice;

public record NoticeResponse(
    Long id, String title, String subTitle, String contents, LocalDateTime createdDate) {

  public static NoticeResponse from(Notice notice) {
    return new NoticeResponse(
        notice.id(), notice.title(), notice.subTitle(), notice.contents(), notice.createdDate());
  }
}
