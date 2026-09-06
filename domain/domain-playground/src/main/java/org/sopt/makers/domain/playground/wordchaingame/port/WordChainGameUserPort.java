package org.sopt.makers.domain.playground.wordchaingame.port;

import java.util.List;

public interface WordChainGameUserPort {

  List<UserInfo> getUserInfosByIds(List<Long> userIds);

  record UserInfo(Long id, String name, String profileImage) {}
}
