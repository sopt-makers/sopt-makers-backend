package org.sopt.makers.domain.app.operationconfig.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;

public interface OperationConfigPort {

  List<OperationConfig> findAllByCategory(OperationConfigCategory category);

  Optional<OperationConfig> findByCategoryAndKey(OperationConfigCategory category, String key);

  OperationConfig save(OperationConfig operationConfig);
}
