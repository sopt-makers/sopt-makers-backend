package org.sopt.makers.domain.crew.dining.query;

import lombok.Getter;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;

@Getter
public class GetDiningsQuery extends PageOptionsDto {

  public static final String TAB_ALL = "ALL";
  public static final String TAB_MY = "MY";
  public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
  public static final String STATUS_ENDED = "ENDED";

  private final String tab;

  private final String status;

  private final MeetingJoinablePart part;

  private final String query;

  public GetDiningsQuery(
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
}
