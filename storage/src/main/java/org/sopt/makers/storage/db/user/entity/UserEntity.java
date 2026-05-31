package org.sopt.makers.storage.db.user.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "UK_AUTH_PLATFORM_ID_AND_AUTH_PLATFORM_TYPE",
          columnNames = {"auth_platform_id", "auth_platform_type"})
    })
public class UserEntity extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String phone;

  private String email;

  private LocalDate birthday;

  @Column(nullable = false)
  private String authPlatformId;

  private String profileImage;

  private boolean isFirstLogin;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OAuthPlatform authPlatformType;

  @Builder(access = PRIVATE)
  private UserEntity(
      String name,
      String phone,
      String email,
      LocalDate birthday,
      String profileImage,
      String authPlatformId,
      OAuthPlatform authPlatformType,
      boolean isFirstLogin) {
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.birthday = birthday;
    this.profileImage = profileImage;
    this.authPlatformId = authPlatformId;
    this.authPlatformType = authPlatformType;
    this.isFirstLogin = isFirstLogin;
  }

  public static UserEntity create(
      String name,
      String phone,
      String email,
      LocalDate birthday,
      String profileImage,
      String authPlatformId,
      OAuthPlatform authPlatformType) {
    return UserEntity.builder()
        .name(name)
        .phone(phone)
        .email(email)
        .birthday(birthday)
        .profileImage(profileImage)
        .authPlatformId(authPlatformId)
        .authPlatformType(authPlatformType)
        .isFirstLogin(true)
        .build();
  }

  public void updateProfile(
      String name, String phone, String email, LocalDate birthday, String profileImage) {
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.birthday = birthday;
    this.profileImage = profileImage;
  }

  public void updateAuthPlatform(String authPlatformId, OAuthPlatform authPlatformType) {
    this.authPlatformId = authPlatformId;
    this.authPlatformType = authPlatformType;
  }

  public void completeFirstLogin() {
    this.isFirstLogin = false;
  }

  public User toDomain() {
    SocialAccount socialAccount = SocialAccount.of(authPlatformId, authPlatformType);
    Profile profile = Profile.of(name, email, phone, birthday, profileImage);
    return User.createUser(getId(), socialAccount, profile, isFirstLogin);
  }

  public User toDomainWithActivities(List<UserActivityHistoryEntity> activities) {
    SocialAccount socialAccount = SocialAccount.of(authPlatformId, authPlatformType);
    Profile profile = Profile.of(name, email, phone, birthday, profileImage);
    if (activities.isEmpty()) {
      return User.createUser(getId(), socialAccount, profile, isFirstLogin);
    }
    ActivityList activityList =
        ActivityList.of(activities.stream().map(UserActivityHistoryEntity::toDomain).toList());
    return User.createUser(getId(), socialAccount, profile, activityList, isFirstLogin);
  }

  public static UserEntity fromDomain(User user) {
    Profile profile = user.profile();
    SocialAccount socialAccount = user.socialAccount();
    UserEntity entity =
        UserEntity.builder()
            .name(profile.name())
            .phone(profile.phone())
            .email(profile.email().orElse(null))
            .birthday(profile.birthday())
            .profileImage(profile.profileImage().orElse(null))
            .authPlatformId(socialAccount.authPlatformId())
            .authPlatformType(socialAccount.authPlatformType())
            .isFirstLogin(user.isFirstLogin())
            .build();
    if (user.id() != null) {
      entity.setId(user.id());
    }
    return entity;
  }
}
