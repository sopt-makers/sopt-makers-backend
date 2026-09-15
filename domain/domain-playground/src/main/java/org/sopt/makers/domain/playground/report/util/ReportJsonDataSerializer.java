package org.sopt.makers.domain.playground.report.util;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public final class ReportJsonDataSerializer {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private ReportJsonDataSerializer() {}

  public static Object serialize(String rawData) {
    Object jsonObject;

    try {
      jsonObject = objectMapper.readValue(rawData, Object.class);
    } catch (RuntimeException e) {
      log.error("Error parsing JSON data ::", e);
      return "";
    }

    if (jsonObject instanceof String str) {
      if (str.matches("-?\\d+(\\.\\d+)?")) {
        return Double.parseDouble(str);
      }
      return str;
    }

    return jsonObject;
  }
}
