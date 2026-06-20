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
    Boolean openToWork,
    Boolean openToSideProject,
    Boolean allowOfficial,
    Boolean hasProfile,
    Boolean editActivitiesAble,
    Boolean isPhoneBlind,
    WorkPreference workPreference,
    List<UserLink> links,
    List<UserCareer> careers) {

  public static Profile of(String name, String email, String phone, LocalDate birthday) {
    validate(name, phone);
    return new Profile(
        name, email, phone, birthday, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, List.of(), List.of());
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
        null,
        null,
        null,
        null,
        List.of(),
        List.of());
  }

  public static Profile ofFull(
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
      Boolean openToWork,
      Boolean openToSideProject,
      Boolean allowOfficial,
      Boolean hasProfile,
      Boolean editActivitiesAble,
      Boolean isPhoneBlind,
      WorkPreference workPreference,
      List<UserLink> links,
      List<UserCareer> careers) {
    validate(name, phone);
    return new Profile(
        name,
        email,
        phone,
        birthday,
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
        openToWork,
        openToSideProject,
        allowOfficial,
        hasProfile,
        editActivitiesAble,
        isPhoneBlind,
        workPreference,
        links,
        careers);
  }

  public Profile updateProfile(
      String email, String phone, LocalDate birthday, String profileImage) {
    if (phone == null || phone.isBlank()) {
      throw new UserException(INVALID_PROFILE_PHONE);
    }
    return new Profile(
        this.name,
        email,
        phone,
        birthday,
        profileImage,
        this.address,
        this.university,
        this.major,
        this.introduction,
        this.mbti,
        this.mbtiDescription,
        this.sojuCapacity,
        this.interest,
        this.userFavor,
        this.idealType,
        this.selfIntroduction,
        this.skill,
        this.openToWork,
        this.openToSideProject,
        this.allowOfficial,
        this.hasProfile,
        this.editActivitiesAble,
        this.isPhoneBlind,
        this.workPreference,
        this.links,
        this.careers);
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
