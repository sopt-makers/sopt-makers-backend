package org.sopt.makers.domain.playground.coffeechat.port;

import java.util.List;
import java.util.Optional;

public interface CoffeeChatUserPort {

  UserDetail getUserDetail(Long userId);

  List<UserDetail> getUserDetails(List<Long> userIds);

  record UserDetail(
      Long id,
      String name,
      String profileImage,
      String phone,
      String email,
      Boolean isPhoneBlind,
      String university,
      List<ActivityInfo> activities,
      Optional<CareerDetail> lastCareer) {}

  record CareerDetail(String companyName, String title) {}

  record ActivityInfo(int generation, String part, boolean isSopt) {}
}
