package org.sopt.makers.api.controller.official.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.sopt.makers.domain.official.review.CreateReviewCommand;
import org.sopt.makers.domain.official.review.ReviewPart;
import org.sopt.makers.domain.official.review.ReviewSearchCondition;

public final class ReviewRequest {

  private ReviewRequest() {}

  @Schema(description = "활동후기 생성 요청")
  public record Create(
      @Schema(description = "기수", example = "34") @NotNull @Positive Integer generation,
      @Schema(description = "파트", example = "SERVER") @NotBlank String part,
      @Schema(description = "메인 카테고리", example = "전체 활동") @NotBlank String mainCategory,
      @Schema(description = "세부 활동 목록", example = "[\"세미나\", \"프로젝트\"]")
          List<String> subActivities,
      @Schema(description = "세부 리크루팅", example = "서류") String subRecruiting,
      @Schema(description = "작성자명", example = "홍길동") @NotBlank String author,
      @Schema(description = "작성자 프로필 이미지 URL") String authorProfileImageUrl,
      @Schema(description = "활동후기 URL") @NotBlank String link) {

    public CreateReviewCommand toCommand() {
      return new CreateReviewCommand(
          generation,
          ReviewPart.from(part),
          mainCategory,
          subActivities,
          subRecruiting,
          author,
          authorProfileImageUrl,
          link);
    }
  }

  @Schema(description = "활동후기 검색 요청")
  public record Search(
      @Schema(description = "카테고리", example = "전체 활동") String category,
      @Schema(description = "활동", example = "세미나") String activity,
      @Schema(description = "파트", example = "SERVER") String part,
      @Schema(description = "기수", example = "34") Integer generation,
      @Schema(description = "페이지 번호", example = "1") @Min(1) Integer pageNo,
      @Schema(description = "페이지 크기", example = "10") @Min(1) @Max(100) Integer limit) {

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_LIMIT = 10;

    public ReviewSearchCondition toCondition() {
      return new ReviewSearchCondition(category, activity, ReviewPart.fromNullable(part), generation);
    }

    public int pageNoOrDefault() {
      return pageNo == null ? DEFAULT_PAGE_NO : pageNo;
    }

    public int limitOrDefault() {
      return limit == null ? DEFAULT_LIMIT : limit;
    }

    public long offset() {
      return (long) (pageNoOrDefault() - 1) * limitOrDefault();
    }
  }

  @Schema(description = "작성자별 활동후기 조회 요청")
  public record Author(
      @Schema(description = "작성자명", example = "홍길동") @NotBlank String name) {}
}
