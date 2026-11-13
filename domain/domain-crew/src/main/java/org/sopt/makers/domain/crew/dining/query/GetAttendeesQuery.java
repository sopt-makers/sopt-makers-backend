package org.sopt.makers.domain.crew.dining.query;

import lombok.Getter;

@Getter
public class GetAttendeesQuery extends PageOptionsDto {

  private final String date;

  public GetAttendeesQuery(Integer page, Integer take, String date) {
    super(page, take == null ? 10 : take);
    this.date = date == null ? "desc" : date;
  }

  @Override
  public Integer getTake() {
    return super.getTake();
  }

  public boolean isValidTake() {
    return getTake() == 10 || getTake() == 30 || getTake() == 50;
  }
}
