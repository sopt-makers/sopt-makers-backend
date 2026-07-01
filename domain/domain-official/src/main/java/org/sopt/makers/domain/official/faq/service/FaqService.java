package org.sopt.makers.domain.official.faq.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.faq.port.FaqRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

  private final FaqRepositoryPort faqRepositoryPort;

  public List<Faq> findAll() {
    return faqRepositoryPort.findAll();
  }

  @Transactional
  public void bulkCreate(List<BulkCreateFaqData> faqs) {
    faqRepositoryPort.deleteAll();
    List<Faq> faqList =
        faqs.stream()
            .map(
                data ->
                    new Faq(
                        null,
                        Part.valueOf(data.part()),
                        data.questions().stream()
                            .map(q -> new Faq.QuestionAnswer(q.question(), q.answer()))
                            .toList()))
            .toList();
    faqRepositoryPort.saveAll(faqList);
  }

  public record BulkCreateFaqData(String part, List<QuestionData> questions) {
    public record QuestionData(String question, String answer) {}
  }
}
