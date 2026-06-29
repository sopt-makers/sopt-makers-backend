package org.sopt.makers.domain.official.faq;

import java.util.List;
import org.sopt.makers.core.type.Part;

public record Faq(Long id, Part part, List<QuestionAnswer> questions) {

  public record QuestionAnswer(String question, String answer) {}
}
