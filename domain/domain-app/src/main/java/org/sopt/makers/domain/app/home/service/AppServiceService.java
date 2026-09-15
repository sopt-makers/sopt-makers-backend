package org.sopt.makers.domain.app.home.service;

import static org.sopt.makers.domain.app.home.exception.HomeFailure.NOT_FOUND_APP_SERVICE;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.AppServiceName;
import org.sopt.makers.domain.app.home.exception.HomeException;
import org.sopt.makers.domain.app.home.port.AppServiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppServiceService {

  private static final Set<AppServiceName> HIDDEN_APP_SERVICES =
      Set.of(AppServiceName.OTHERS, AppServiceName.FLOATING_BUTTON, AppServiceName.REVIEW_FORM);
  private static final Set<AppServiceName> HOME_APP_SERVICES = Set.of(AppServiceName.SOPT_LETTER);
  private static final Set<AppServiceName> TAB_APP_SERVICES =
      Set.of(AppServiceName.POKE, AppServiceName.SOPTAMP);

  private final AppServiceRepositoryPort appServiceRepositoryPort;

  public List<AppService> getHomeAppServices() {
    return getAppServices(HOME_APP_SERVICES);
  }

  public List<AppService> getTabAppServices() {
    return getAppServices(TAB_APP_SERVICES);
  }

  public List<AppService> getAllAppService() {
    return appServiceRepositoryPort.findAll().stream()
        .filter(
            appService ->
                !HIDDEN_APP_SERVICES.contains(AppServiceName.of(appService.serviceName())))
        .sorted(Comparator.comparing(AppService::createdAt).reversed())
        .toList();
  }

  public AppService getAppService(String serviceName) {
    return appServiceRepositoryPort
        .findByServiceName(serviceName)
        .orElseThrow(() -> new HomeException(NOT_FOUND_APP_SERVICE));
  }

  private List<AppService> getAppServices(Set<AppServiceName> appServiceNames) {
    return getAllAppService().stream()
        .filter(appService -> appServiceNames.contains(AppServiceName.of(appService.serviceName())))
        .toList();
  }
}
