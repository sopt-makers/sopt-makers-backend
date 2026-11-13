package org.sopt.makers.api.controller.crew.dining.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class GetAttendeesRequest extends DiningPageRequest {

  @Schema(
      description = "등록 시각 정렬 방향",
      defaultValue = "desc",
      allowableValues = {"asc", "desc"})
  @Pattern(regexp = "asc|desc")
  private final String date;

  public GetAttendeesRequest(Integer page, Integer take, String date) {
    super(page, take == null ? 10 : take);
    this.date = date == null ? "desc" : date;
  }

  @Override
  @Schema(
      description = "가져올 데이터 개수",
      defaultValue = "10",
      allowableValues = {"10", "30", "50"})
  public Integer getTake() {
    return super.getTake();
  }

  @AssertTrue(message = "조회 개수는 10, 30, 50 중 하나여야 합니다.")
  public boolean isValidTake() {
    return getTake() == 10 || getTake() == 30 || getTake() == 50;
  }

  public org.sopt.makers.domain.crew.dining.query.GetAttendeesQuery toQuery() {
    return new org.sopt.makers.domain.crew.dining.query.GetAttendeesQuery(
        getPage(), getTake(), getDate());
  }
}
