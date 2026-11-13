package org.sopt.makers.domain.crew.dining.query;

import lombok.Getter;

@Getter
public class PageOptionsDto {

  private Integer page;

  private Integer take;

  public PageOptionsDto(Integer page, Integer take) {
    this.page = page == null ? 1 : page;
    this.take = take == null ? 12 : take;
  }
}
