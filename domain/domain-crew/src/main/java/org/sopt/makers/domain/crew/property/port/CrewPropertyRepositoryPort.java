package org.sopt.makers.domain.crew.property.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.property.CrewProperty;

public interface CrewPropertyRepositoryPort {

  Optional<CrewProperty> findByKey(String key);

  List<CrewProperty> findAll();
}
