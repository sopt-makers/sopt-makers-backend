package org.sopt.makers.domain.crew.soptmap;

import java.util.Arrays;
import java.util.List;

public enum SubwayLine {
  LINE_1("1호선"),
  LINE_2("2호선"),
  LINE_3("3호선"),
  LINE_4("4호선"),
  LINE_5("5호선"),
  LINE_6("6호선"),
  LINE_7("7호선"),
  LINE_8("8호선"),
  LINE_9("9호선"),
  INCHEON_1("인천1호선"),
  INCHEON_2("인천2호선"),
  SINBUNDANG("신분당선"),
  SUIN_BUNDANG("수인분당선"),
  GYEONGUI_JUNGANG("경의중앙선"),
  GYEONGCHUN("경춘선"),
  GYEONGGANG("경강선"),
  UI_SINSEOL("우이신설선", "우이신설경전철"),
  SILLIM("신림선", "신림경전철"),
  EVERLINE("에버라인", "용인경전철"),
  GIMPO_GOLD("김포골드라인"),
  SEOHAE("서해선"),
  AIRPORT_RAILROAD("공항철도"),
  UIJEONGBU("의정부경전철"),
  GTX_A("GTX-A", "지티엑스A");

  private final String value;
  private final List<String> aliases;

  SubwayLine(String value, String... aliases) {
    this.value = value;
    this.aliases = List.of(aliases);
  }

  public String getValue() {
    return value;
  }

  public static SubwayLine fromValue(String input) {
    return Arrays.stream(values())
        .filter(
            line ->
                line.value.equals(input)
                    || line.aliases.contains(input)
                    || line.name().equals(input))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 지하철 노선입니다: " + input));
  }
}
