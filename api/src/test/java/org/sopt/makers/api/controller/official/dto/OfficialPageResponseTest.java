package org.sopt.makers.api.controller.official.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OfficialPageResponseTest {

  @ParameterizedTest(name = "{0}: totalCount={1}, limit={2}, currentPage={3}")
  @CsvSource({
    "한 페이지보다 적은 결과, 5, 10, 1, 1, false, false",
    "limit의 정확한 배수인 마지막 페이지, 20, 10, 2, 2, false, true",
    "limit의 정확한 배수인 첫 페이지, 20, 10, 1, 2, true, false",
    "전체 페이지를 넘는 요청, 25, 10, 5, 3, false, true",
    "결과 0건에서 두 번째 페이지 요청, 0, 10, 2, 0, false, true"
  })
  @DisplayName("페이지 정보를 레거시 PaginateResponseDto와 같은 방식으로 계산한다")
  void calculatesLegacyPageInfo(
      String label,
      int totalCount,
      int limit,
      int currentPage,
      int totalPage,
      boolean hasNextPage,
      boolean hasPrevPage) {
    OfficialPageResponse<String> response =
        OfficialPageResponse.of(List.of(), totalCount, limit, currentPage);

    assertThat(response.totalCount()).isEqualTo(totalCount);
    assertThat(response.limit()).isEqualTo(limit);
    assertThat(response.currentPage()).isEqualTo(currentPage);
    assertThat(response.totalPage()).isEqualTo(totalPage);
    assertThat(response.hasNextPage()).isEqualTo(hasNextPage);
    assertThat(response.hasPrevPage()).isEqualTo(hasPrevPage);
  }
}
