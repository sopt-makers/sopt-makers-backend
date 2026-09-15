package org.sopt.makers.domain.app.calendar.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.calendar.Calendar;

public interface CalendarCacheRepositoryPort {

  Optional<List<Calendar>> findByGeneration(int generation);

  void save(int generation, List<Calendar> calendars);
}
