package org.sopt.makers.domain.app.home.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.home.AppService;

public interface AppServiceRepositoryPort {

  List<AppService> findAll();

  Optional<AppService> findByServiceName(String serviceName);
}
