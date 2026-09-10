package org.sopt.makers.domain.playground.member.profile.port;

public interface UserProfileNotifierPort {

  void notifyNewProfile(Long userId, String name, String idealType);
}
