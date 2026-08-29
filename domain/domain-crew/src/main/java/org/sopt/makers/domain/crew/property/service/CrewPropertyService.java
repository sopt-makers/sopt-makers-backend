package org.sopt.makers.domain.crew.property.service;

import static org.sopt.makers.domain.crew.property.exception.CrewPropertyFailure.INVALID_HOME_PROPERTY;
import static org.sopt.makers.domain.crew.property.exception.CrewPropertyFailure.NOT_FOUND_CREW_PROPERTY;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.property.CrewProperty;
import org.sopt.makers.domain.crew.property.HomeContent;
import org.sopt.makers.domain.crew.property.exception.CrewPropertyException;
import org.sopt.makers.domain.crew.property.port.CrewPropertyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewPropertyService {

  private static final String HOME_PROPERTY_KEY = "home";
  private static final String HOME_CONTENTS_KEY = "home";
  private static final String TITLE_KEY = "title";
  private static final String MEETING_IDS_KEY = "meetingIds";

  private final CrewPropertyRepositoryPort crewPropertyRepositoryPort;

  public Map<String, Object> getValues(String key) {
    return getByKey(key).values();
  }

  public List<Map<String, Object>> getAllValues() {
    return crewPropertyRepositoryPort.findAll().stream().map(CrewProperty::values).toList();
  }

  public List<HomeContent> getHomeContents() {
    Object contents = getByKey(HOME_PROPERTY_KEY).values().get(HOME_CONTENTS_KEY);
    if (!(contents instanceof List<?> items)) {
      throw new CrewPropertyException(INVALID_HOME_PROPERTY);
    }
    try {
      return items.stream().map(this::toHomeContent).toList();
    } catch (RuntimeException exception) {
      throw new CrewPropertyException(INVALID_HOME_PROPERTY);
    }
  }

  private CrewProperty getByKey(String key) {
    if (key == null || key.isBlank()) {
      throw new CrewPropertyException(NOT_FOUND_CREW_PROPERTY);
    }
    return crewPropertyRepositoryPort
        .findByKey(key)
        .orElseThrow(() -> new CrewPropertyException(NOT_FOUND_CREW_PROPERTY));
  }

  private HomeContent toHomeContent(Object item) {
    if (!(item instanceof Map<?, ?> values)) {
      throw new IllegalArgumentException("홈 컨텐츠는 객체여야 합니다.");
    }
    Object title = values.get(TITLE_KEY);
    Object meetingIds = values.get(MEETING_IDS_KEY);
    if (title == null || !(meetingIds instanceof List<?> ids)) {
      throw new IllegalArgumentException("홈 컨텐츠 필드가 올바르지 않습니다.");
    }
    return new HomeContent(
        String.valueOf(title),
        ids.stream()
            .map(
                id ->
                    id instanceof Number number
                        ? number.longValue()
                        : Long.parseLong(String.valueOf(id)))
            .toList());
  }
}
