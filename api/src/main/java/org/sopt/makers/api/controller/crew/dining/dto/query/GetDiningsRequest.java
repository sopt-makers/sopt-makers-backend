package org.sopt.makers.api.controller.crew.dining.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;

@Getter
public class GetDiningsRequest extends DiningPageRequest {

  public static final String TAB_ALL = "ALL";
  public static final String TAB_MY = "MY";
  public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
  public static final String STATUS_ENDED = "ENDED";

  @Schema(
      description = "목록 탭. ALL: 전체 회식, MY: 내 회식(개설했거나 참석 중인 회식)",
      defaultValue = "ALL",
      allowableValues = {"ALL", "MY"})
  @Pattern(regexp = "ALL|MY")
  private final String tab;

  @Schema(
      description = "진행 상태. 생략하면 전체 상태",
      allowableValues = {"IN_PROGRESS", "ENDED"})
  @Pattern(regexp = "IN_PROGRESS|ENDED")
  private final String status;

  @Schema(description = "파트. 생략하면 전체 파트이며, 전체 파트 회식은 모든 파트 필터에 포함된다", example = "SERVER")
  private final MeetingJoinablePart part;

  @Schema(description = "회식명 검색어", example = "서버")
  @Size(max = 30)
  private final String query;

  public GetDiningsRequest(
      Integer page,
      Integer take,
      String tab,
      String status,
      MeetingJoinablePart part,
      String query) {
    super(page, take);
    this.tab = tab == null ? TAB_ALL : tab;
    this.status = status;
    this.part = part;
    this.query = query;
  }

  public boolean isMyTab() {
    return TAB_MY.equals(tab);
  }

  public org.sopt.makers.domain.crew.dining.query.GetDiningsQuery toQuery() {
    return new org.sopt.makers.domain.crew.dining.query.GetDiningsQuery(
        getPage(), getTake(), getTab(), getStatus(), getPart(), getQuery());
  }
}
