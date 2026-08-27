package org.sopt.makers.clients.dictionary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.wordchaingame.port.DictionaryPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DictionaryAdapter implements DictionaryPort {

  private static final String OPENDICT_API_URL =
      "https://opendict.korean.go.kr/api/search?key={key}&req_type=json&q={word}";

  private final RestTemplate restTemplate;
  private final DictionaryProperty dictionaryProperty;
  private final ObjectMapper objectMapper;

  @Override
  public boolean isValidWord(String word) {
    try {
      String sanitizedWord = word.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z]", "");
      String response =
          restTemplate.getForObject(
              OPENDICT_API_URL, String.class, dictionaryProperty.key(), sanitizedWord);
      return isNoun(response);
    } catch (Exception e) {
      log.error("사전 API 호출 중 오류 발생: {}", e.getMessage());
      return false;
    }
  }

  private boolean isNoun(String response) {
    try {
      JsonNode root = objectMapper.readTree(response);
      JsonNode channel = root.get("channel");
      if (channel == null) return false;
      JsonNode items = channel.get("item");
      if (items == null || !items.isArray() || items.isEmpty()) return false;
      JsonNode senses = items.get(0).get("sense");
      if (senses == null || !senses.isArray() || senses.isEmpty()) return false;
      JsonNode pos = senses.get(0).get("pos");
      return pos != null && "명사".equals(pos.asText());
    } catch (Exception e) {
      log.error("사전 API 응답 파싱 중 오류 발생: {}", e.getMessage());
      return false;
    }
  }
}
