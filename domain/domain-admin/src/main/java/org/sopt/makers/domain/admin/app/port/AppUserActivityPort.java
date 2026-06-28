package org.sopt.makers.domain.admin.app.port;

import java.util.Optional;
import org.sopt.makers.domain.admin.app.AppUserActivity;

public interface AppUserActivityPort {

  Optional<AppUserActivity> findCurrentActivity(Long userId);
}
