package org.sopt.makers.domain.user.port;

import java.util.List;

public interface PlaygroundProjectUserPort {

  List<ProjectUserInfo> getProjectUserInfosByIds(List<Long> userIds);

  record ProjectUserInfo(
      Long id, String name, String profileImage, List<Integer> generations, boolean hasProfile) {}
}
