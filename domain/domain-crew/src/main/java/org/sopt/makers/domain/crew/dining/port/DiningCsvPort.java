package org.sopt.makers.domain.crew.dining.port;

import java.util.List;
import org.sopt.makers.domain.crew.dining.model.DiningUser;

public interface DiningCsvPort {
  String upload(List<DiningUser> attendees);
}
