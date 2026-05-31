package org.sopt.makers.domain.user;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_PROFILE_NAME;
import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_PROFILE_PHONE;

import java.time.LocalDate;
import java.util.Optional;
import org.sopt.makers.domain.user.exception.UserException;

public record Profile(
    String name,
    Optional<String> email,
    String phone,
    LocalDate birthday,
    Optional<String> profileImage) {

  public static Profile of(String name, String email, String phone, LocalDate birthday) {
    validate(name, phone);
    return new Profile(name, Optional.ofNullable(email), phone, birthday, Optional.empty());
  }

  public static Profile of(
      String name, String email, String phone, LocalDate birthday, String profileImage) {
    validate(name, phone);
    return new Profile(
        name, Optional.ofNullable(email), phone, birthday, Optional.ofNullable(profileImage));
  }

  public Profile updateProfile(
      String email, String phone, LocalDate birthday, String profileImage) {
    if (phone == null || phone.isBlank()) {
      throw new UserException(INVALID_PROFILE_PHONE);
    }
    return new Profile(
        this.name, Optional.ofNullable(email), phone, birthday, Optional.ofNullable(profileImage));
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
