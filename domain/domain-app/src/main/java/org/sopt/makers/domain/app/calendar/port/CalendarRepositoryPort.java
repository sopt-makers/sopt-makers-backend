package org.sopt.makers.domain.app.calendar.port;

import java.util.List;
import org.sopt.makers.domain.app.calendar.Calendar;

public interface CalendarRepositoryPort {

  List<Calendar> findAllByGenerationOrderByStartDateAscEndDateAsc(int generation);
}
