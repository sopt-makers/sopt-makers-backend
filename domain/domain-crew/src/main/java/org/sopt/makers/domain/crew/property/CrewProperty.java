package org.sopt.makers.domain.crew.property;

import java.util.Map;

public record CrewProperty(Long id, String key, Map<String, Object> values) {

  public CrewProperty {
    values = values == null ? Map.of() : Map.copyOf(values);
  }
}
