package org.sopt.makers.clients.s3;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.dining.model.DiningUser;

class DiningCsvAdapterTest {
  @Test
  void 한글_BOM_따옴표_줄바꿈_전화번호와_수식문자를_보존한다() {
    String csv =
        new String(
            DiningCsvAdapter.toCsv(
                List.of(
                    new DiningUser(1L, "=HYPERLINK(\"x\")", null, "01012345678", 39),
                    new DiningUser(2L, "이름,\n다음", null, null, null))),
            StandardCharsets.UTF_8);
    assertThat(csv).startsWith("\uFEFF\"번호\",\"이름\",\"최근 기수\",\"연락처\"\n");
    assertThat(csv).contains("\"\t=HYPERLINK(\"\"x\"\")\"", "\"\t01012345678\"", "\"이름,\n다음\"");
  }

  @Test
  void 빈목록도_헤더를_포함한다() {
    assertThat(new String(DiningCsvAdapter.toCsv(List.of()), StandardCharsets.UTF_8))
        .contains("연락처");
  }
}
