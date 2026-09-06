package org.sopt.makers.storage.redis.playground.member.profile.cache;

import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.storage.redis.user.cache.CachedUserActivity;

public record CachedMemberProfileCard(
    Long userId,
    String name,
    String profileImage,
    LocalDate birthday,
    String phone,
    String email,
    String address,
    String university,
    String major,
    String introduction,
    String mbti,
    String mbtiDescription,
    Double sojuCapacity,
    String interest,
    String idealType,
    String selfIntroduction,
    String skill,
    Boolean allowOfficial,
    Boolean isPhoneBlind,
    Boolean isPourSauceLover,
    Boolean isHardPeachLover,
    Boolean isMintChocoLover,
    Boolean isRedBeanFishBreadLover,
    Boolean isSojuLover,
    Boolean isRiceTteokLover,
    List<CachedUserActivity> activities,
    List<CachedMemberLink> links,
    List<CachedMemberCareer> careers) {

  public record CachedMemberLink(Long id, String title, String url) {}

  public record CachedMemberCareer(
      Long id, String companyName, String title, String startDate, String endDate, Boolean isCurrent) {}
}
