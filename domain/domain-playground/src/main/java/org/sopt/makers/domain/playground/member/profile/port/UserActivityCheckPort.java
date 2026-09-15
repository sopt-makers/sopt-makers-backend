package org.sopt.makers.domain.playground.member.profile.port;

public interface UserActivityCheckPort {

  /** 확인 이력이 없는 유저는 레거시 기본값(true)을 그대로 따른다. */
  boolean isEditActivitiesAble(Long userId);

  void updateEditActivitiesAble(Long userId, boolean isCheck);
}
