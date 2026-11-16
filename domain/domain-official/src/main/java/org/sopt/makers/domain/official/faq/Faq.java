package org.sopt.makers.domain.official.faq;

import java.util.List;

public record Faq(Long id, FaqCategory category, List<QuestionAnswer> questions) {

  public record QuestionAnswer(String question, String answer) {}
}
