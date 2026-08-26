package org.sopt.makers.storage.db.crew.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.SoptMapEventPolicy;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapEventPolicyPort;
import org.sopt.makers.storage.db.crew.repository.SoptMapPropertyJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoptMapEventPolicyAdapter implements SoptMapEventPolicyPort {

  private static final String PROPERTY_KEY = "soptMapEvent";
  private static final String START_DATE_KEY = "startDate";
  private static final String END_DATE_KEY = "endDate";
  private static final String EVENT_NUMBERS_KEY = "eventNumbers";

  private final SoptMapPropertyJpaRepository repository;

  @Override
  public Optional<SoptMapEventPolicy> findPolicy() {
    return repository
        .findByKey(PROPERTY_KEY)
        .flatMap(
            property -> {
              try {
                Map<String, Object> values = property.getProperties();
                return Optional.of(
                    new SoptMapEventPolicy(
                        parseDate(values.get(START_DATE_KEY)),
                        parseDate(values.get(END_DATE_KEY)),
                        parseWinnerOrders(values.get(EVENT_NUMBERS_KEY))));
              } catch (RuntimeException exception) {
                return Optional.empty();
              }
            });
  }

  private LocalDate parseDate(Object value) {
    String[] dateParts = String.valueOf(value).split("[-/.]");
    if (dateParts.length != 3) {
      throw new IllegalArgumentException("솝맵 이벤트 날짜 형식이 올바르지 않습니다.");
    }
    return LocalDate.of(
        Integer.parseInt(dateParts[0]),
        Integer.parseInt(dateParts[1]),
        Integer.parseInt(dateParts[2]));
  }

  private List<Integer> parseWinnerOrders(Object value) {
    if (!(value instanceof List<?> values)) {
      return List.of();
    }
    return values.stream()
        .map(
            item ->
                item instanceof Number number
                    ? number.intValue()
                    : Integer.parseInt(String.valueOf(item)))
        .toList();
  }
}
