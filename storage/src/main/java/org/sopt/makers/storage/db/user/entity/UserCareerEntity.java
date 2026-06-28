package org.sopt.makers.storage.db.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_careers")
public class UserCareerEntity extends BaseEntity {

  private Long userId;

  private String companyName;

  private String title;

  private String startDate;

  private String endDate;

  private Boolean isCurrent;

  @Builder(access = AccessLevel.PRIVATE)
  private UserCareerEntity(
      final Long userId,
      final String companyName,
      final String title,
      final String startDate,
      final String endDate,
      final Boolean isCurrent) {
    this.userId = userId;
    this.companyName = companyName;
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
    this.isCurrent = isCurrent;
  }

  public static UserCareerEntity from(final UserCareer userCareer) {
    return UserCareerEntity.builder()
        .userId(userCareer.userId())
        .companyName(userCareer.companyName())
        .title(userCareer.title())
        .startDate(userCareer.startDate())
        .endDate(userCareer.endDate())
        .isCurrent(userCareer.isCurrent())
        .build();
  }

  public UserCareer toDomain() {
    return UserCareer.of(getId(), userId, companyName, title, startDate, endDate, isCurrent);
  }
}
