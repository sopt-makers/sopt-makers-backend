package org.sopt.makers.domain.crew.dining.port;

import java.util.List;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.dining.query.GetDiningsQuery;

public interface DiningSearchPort {
  long countDinings(GetDiningsQuery query, Long userId);

  List<DiningMeeting> findDinings(GetDiningsQuery query, Long userId);
}
