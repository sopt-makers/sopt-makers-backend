package org.sopt.makers.api.common.converter;

import org.sopt.makers.domain.playground.member.ask.AskTab;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StringToAskTabConverter implements Converter<String, AskTab> {

  @Override
  public AskTab convert(@NonNull String source) {
    return AskTab.from(source);
  }
}
