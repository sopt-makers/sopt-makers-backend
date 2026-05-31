package org.sopt.makers.domain.user;

import java.time.LocalDate;
import org.sopt.makers.core.type.Part;

public record UserRegisterInfo(
    String name, String phone, String email, LocalDate birthday, int generation, Part part) {
  public static UserRegisterInfo of(
      String name, String phone, String email, LocalDate birthday, int generation, Part part) {
    return new UserRegisterInfo(name, phone, email, birthday, generation, part);
  }
}
