package org.sopt.makers.api.common.converter;

import org.sopt.makers.domain.playground.member.ask.QuestionTab;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StringToQuestionTabConverter implements Converter<String, QuestionTab> {

  @Override
  public QuestionTab convert(@NonNull String source) {
    return QuestionTab.from(source);
  }
}
