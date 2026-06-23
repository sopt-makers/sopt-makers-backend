package org.sopt.makers.api.controller.official.homepage.review.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.homepage.review.service.HomepageReviewService;

@NoArgsConstructor(access = PRIVATE)
public final class HomepageReviewRequest {

  public record Create(
      @NotBlank(message = "제목은 필수 입력 값입니다.") @Size(max = 10, message = "제목은 최대 10자까지 입력 가능합니다.")
          String title,
      @NotBlank(message = "내용은 필수 입력 값입니다.") @Size(max = 200, message = "내용은 최대 200자까지 입력 가능합니다.")
          String content,
      @NotBlank(message = "작성자 정보는 필수 입력 값입니다.") String authorInfo) {

    public HomepageReviewService.CreateHomepageReviewCommand toCommand() {
      return new HomepageReviewService.CreateHomepageReviewCommand(title, content, authorInfo);
    }
  }

  public record Edit(
      @NotBlank(message = "제목은 필수 입력 값입니다.") @Size(max = 10, message = "제목은 최대 10자까지 입력 가능합니다.")
          String title,
      @NotBlank(message = "내용은 필수 입력 값입니다.") @Size(max = 200, message = "내용은 최대 200자까지 입력 가능합니다.")
          String content,
      @NotBlank(message = "작성자 정보는 필수 입력 값입니다.") String authorInfo) {

    public HomepageReviewService.EditHomepageReviewCommand toCommand() {
      return new HomepageReviewService.EditHomepageReviewCommand(title, content, authorInfo);
    }
  }
}
