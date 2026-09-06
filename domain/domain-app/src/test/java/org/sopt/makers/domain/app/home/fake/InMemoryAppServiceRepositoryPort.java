package org.sopt.makers.domain.app.home.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.port.AppServiceRepositoryPort;

public final class InMemoryAppServiceRepositoryPort implements AppServiceRepositoryPort {

  private final List<AppService> store = new ArrayList<>();

  public void add(AppService appService) {
    store.add(appService);
  }

  @Override
  public List<AppService> findAll() {
    return List.copyOf(store);
  }

  @Override
  public Optional<AppService> findByServiceName(String serviceName) {
    return store.stream().filter(s -> s.serviceName().equals(serviceName)).findFirst();
  }
}
