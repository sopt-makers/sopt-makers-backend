package org.sopt.makers.domain.official.recruit.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.corevalue.service.CoreValueService;
import org.sopt.makers.domain.official.faq.service.FaqService;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.generation.service.GenerationService;
import org.sopt.makers.domain.official.part.service.PartService;
import org.sopt.makers.domain.official.recruit.*;
import org.sopt.makers.domain.official.recruit.service.RecruitPartIntroductionService;

class RecruitPageFacadeTest {
  @Test
  @DisplayName("파트 상세는 파트 소개와 선호 인재상 및 커리큘럼을 조합한다")
  void getsPartDetail() {
    var generations = mock(GenerationService.class);
    var parts = mock(PartService.class);
    var introductions = mock(RecruitPartIntroductionService.class);
    when(generations.findLatest()).thenReturn(new Generation(39, "SOPT", null, null, null, null));
    when(parts.findByGenerationAndPart(39, Part.WEB))
        .thenReturn(new RecruitPartInfo(1L, 39, Part.WEB, "파트 소개", List.of("React")));
    when(introductions.findByGenerationAndPart(39, Part.WEB))
        .thenReturn(new RecruitPartIntroduction(1L, 39, Part.WEB, "모집 소개", List.of("성실한 사람")));
    var facade =
        new RecruitPageFacade(
            generations,
            mock(CoreValueService.class),
            mock(FaqService.class),
            parts,
            introductions);
    assertThat(facade.getRecruitPartDetail(Part.WEB))
        .isEqualTo(
            new RecruitPageFacade.RecruitPartDetail("파트 소개", List.of("성실한 사람"), List.of("React")));
  }
}
