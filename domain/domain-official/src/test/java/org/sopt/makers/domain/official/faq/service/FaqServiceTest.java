package org.sopt.makers.domain.official.faq.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.faq.*;
import org.sopt.makers.domain.official.faq.port.FaqRepositoryPort;

class FaqServiceTest {
  private final FaqRepositoryPort port = mock(FaqRepositoryPort.class);
  private final FaqService service = new FaqService(port);

  @Test
  @DisplayName("홈페이지 FAQ는 카테고리 순서대로 조회한다")
  void categories() {
    when(port.findAll())
        .thenReturn(
            List.of(
                faq(FaqCategory.ACTIVITY),
                faq(FaqCategory.WEB),
                faq(FaqCategory.QUALIFICATION),
                faq(FaqCategory.INTERVIEW)));
    assertThat(service.findCategoryFaqs())
        .extracting(Faq::category)
        .containsExactly(FaqCategory.QUALIFICATION, FaqCategory.INTERVIEW, FaqCategory.ACTIVITY);
  }

  @Test
  @DisplayName("모집 FAQ에는 파트별 질문만 포함한다")
  void parts() {
    when(port.findAll())
        .thenReturn(
            List.of(faq(FaqCategory.WEB), faq(FaqCategory.ACTIVITY), faq(FaqCategory.ANDROID)));
    assertThat(service.findAll())
        .extracting(Faq::category)
        .containsExactly(FaqCategory.ANDROID, FaqCategory.WEB);
  }

  @Test
  @DisplayName("FAQ 교체 시 기존 FAQ를 모두 삭제하고 새 목록을 저장한다")
  void replacesAllFaqs() {
    service.bulkCreate(
        List.of(new FaqService.BulkCreateFaqData(FaqCategory.QUALIFICATION, List.of())));
    verify(port).deleteAll();
    verify(port).saveAll(List.of(new Faq(null, FaqCategory.QUALIFICATION, List.of())));
  }

  private Faq faq(FaqCategory category) {
    return new Faq(1L, category, List.of());
  }

  @Test
  @DisplayName("빈 FAQ 목록을 전달하면 전체 FAQ를 삭제한다")
  void clearsAllFaqs() {
    service.bulkCreate(List.of());
    verify(port).deleteAll();
    verify(port).saveAll(List.of());
    verify(port, never()).findAll();
  }
}
