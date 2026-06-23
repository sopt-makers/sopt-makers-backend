package org.sopt.makers.domain.user;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_PROFILE_NAME;
import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_PROFILE_PHONE;

import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.user.exception.UserException;

public record Profile(
    String name,
    String email,
    String phone,
    LocalDate birthday,
    String profileImage,
    String address,
    String university,
    String major,
    String introduction,
    String mbti,
    String mbtiDescription,
    Double sojuCapacity,
    String interest,
    UserFavor userFavor,
    String idealType,
    String selfIntroduction,
    String skill,
    Boolean allowOfficial,
    Boolean isPhoneBlind,
    WorkPreference workPreference,
    List<UserLink> links,
    List<UserCareer> careers) {

  public static Profile of(String name, String email, String phone, LocalDate birthday) {
    validate(name, phone);
    return new Profile(
        name, email, phone, birthday, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, List.of(), List.of());
  }

  public static Profile of(
      String name, String email, String phone, LocalDate birthday, String profileImage) {
    validate(name, phone);
    return new Profile(
        name,
        email,
        phone,
        birthday,
        profileImage,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(),
        List.of());
  }

  /** 유저가 변경 가능한 필드를 적용한 새 Profile을 반환한다. name, birthday는 시스템 관리 필드로 변경 불가. */
  public Profile update(
      String email,
      String phone,
      String profileImage,
      String address,
      String university,
      String major,
      String introduction,
      String skill,
      String mbti,
      String mbtiDescription,
      Double sojuCapacity,
      String interest,
      UserFavor userFavor,
      String idealType,
      String selfIntroduction,
      Boolean allowOfficial,
      Boolean isPhoneBlind,
      WorkPreference workPreference,
      List<UserLink> links,
      List<UserCareer> careers) {
    if (phone == null || phone.isBlank()) {
      throw new UserException(INVALID_PROFILE_PHONE);
    }
    return new Profile(
        this.name, // 시스템 관리 — 변경 불가
        email,
        phone,
        this.birthday, // 시스템 관리 — 변경 불가
        profileImage,
        address,
        university,
        major,
        introduction,
        mbti,
        mbtiDescription,
        sojuCapacity,
        interest,
        userFavor,
        idealType,
        selfIntroduction,
        skill,
        allowOfficial,
        isPhoneBlind,
        workPreference,
        links != null ? links : List.of(),
        careers != null ? careers : List.of());
  }

  private static void validate(String name, String phone) {
    if (name == null || name.isBlank()) {
      throw new UserException(INVALID_PROFILE_NAME);
    }
    if (phone == null || phone.isBlank()) {
      throw new UserException(INVALID_PROFILE_PHONE);
    }
  }
}
