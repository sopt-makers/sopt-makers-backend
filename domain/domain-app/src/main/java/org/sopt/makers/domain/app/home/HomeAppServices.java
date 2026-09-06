package org.sopt.makers.domain.app.home;

import java.util.List;

public record HomeAppServices(boolean isAppjamMode, List<AppServiceEntryStatus> appServices) {}
