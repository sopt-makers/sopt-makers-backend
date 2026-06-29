package org.sopt.makers.domain.official.recruit.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.corevalue.service.CoreValueService;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.faq.service.FaqService;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.generation.service.GenerationService;
import org.sopt.makers.domain.official.part.service.PartService;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;
import org.sopt.makers.domain.official.recruit.service.RecruitPartIntroductionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitPageFacade {

  private final GenerationService generationService;
  private final CoreValueService coreValueService;
  private final FaqService faqService;
  private final PartService partService;
  private final RecruitPartIntroductionService recruitPartIntroductionService;

  public RecruitMainPage getRecruitMainPage() {
    Generation generation = generationService.findLatest();
    Integer generationId = generation.id();

    return new RecruitMainPage(
        generation,
        coreValueService.findByGeneration(generationId),
        partService.findByGeneration(generationId),
        faqService.findAll());
  }

  public RecruitPartDetail getRecruitPartDetail(Part part) {
    Generation generation = generationService.findLatest();
    Integer generationId = generation.id();
    RecruitPartInfo partInfo = partService.findByGenerationAndPart(generationId, part);
    RecruitPartIntroduction introduction =
        recruitPartIntroductionService.findByGenerationAndPart(generationId, part);

    return new RecruitPartDetail(partInfo.description(), introduction.preference(), partInfo.curriculums());
  }

  public record RecruitMainPage(
      Generation generation,
      List<CoreValue> coreValues,
      List<RecruitPartInfo> partIntroductions,
      List<Faq> faqs) {}

  public record RecruitPartDetail(
      String introduction, String preference, List<String> partCurriculum) {}
}
