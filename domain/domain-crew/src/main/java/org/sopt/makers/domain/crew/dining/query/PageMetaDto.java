package org.sopt.makers.domain.crew.dining.query;

import lombok.Getter;

@Getter
public class PageMetaDto {

  private int page;

  private int take;

  private int itemCount;

  private int pageCount;

  private boolean hasPreviousPage;

  private boolean hasNextPage;

  public PageMetaDto(PageOptionsDto pageOptionsDto, int itemCount) {
    this(
        pageOptionsDto.getPage(),
        pageOptionsDto.getTake(),
        itemCount,
        (int) Math.ceil((double) itemCount / pageOptionsDto.getTake()));
  }

  public PageMetaDto(PageOptionsDto pageOptionsDto, int itemCount, int pageCount) {
    this(pageOptionsDto.getPage(), pageOptionsDto.getTake(), itemCount, pageCount);
  }

  public PageMetaDto(int page, int take, int itemCount, int pageCount) {
    this.page = page;
    this.take = take;
    this.itemCount = itemCount;
    this.pageCount = pageCount;
    this.hasPreviousPage = this.page > 1;
    this.hasNextPage = this.page < this.pageCount;
  }
}
