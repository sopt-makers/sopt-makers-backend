package org.sopt.makers.domain.user;

public record UserCareer(
    Long id,
    Long userId,
    String companyName,
    String title,
    String startDate,
    String endDate,
    Boolean isCurrent) {

  public static UserCareer of(
      final Long id,
      final Long userId,
      final String companyName,
      final String title,
      final String startDate,
      final String endDate,
      final Boolean isCurrent) {
    return new UserCareer(id, userId, companyName, title, startDate, endDate, isCurrent);
  }
}
