package org.sopt.makers.domain.user.port;

import java.util.List;

public interface PlaygroundWordChainGameUserPort {

  List<UserInfo> getUserInfosByIds(List<Long> userIds);

  record UserInfo(Long id, String name, String profileImage) {}
}
