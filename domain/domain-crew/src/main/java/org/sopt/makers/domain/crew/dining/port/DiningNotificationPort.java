package org.sopt.makers.domain.crew.dining.port;

import java.util.List;

public interface DiningNotificationPort {
  boolean send(List<String> receivers, String title, String content, String path);
}
