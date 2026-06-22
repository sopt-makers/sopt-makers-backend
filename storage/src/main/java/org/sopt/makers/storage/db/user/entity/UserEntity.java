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
import org.sopt.makers.domain.user.UserFavor;
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

  @Column(nullable = false)
  private Boolean isFirstLogin = true;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OAuthPlatform authPlatformType;

  private String address;

  private String university;

  private String major;

  private String introduction;

  private String mbti;

  private String mbtiDescription;

  private Double sojuCapacity;

  private String interest;

  private Boolean isPourSauceLover;

  private Boolean isHardPeachLover;

  private Boolean isMintChocoLover;

  private Boolean isRedBeanFishBreadLover;

  private Boolean isSojuLover;

  private Boolean isRiceTteokLover;

  private String idealType;

  private String selfIntroduction;

  private String skill;

  private Boolean openToWork;

  private Boolean openToSideProject;

  private Boolean allowOfficial;

  private Boolean editActivitiesAble;

  @Column(nullable = false)
  private Boolean isPhoneBlind = false;

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

  public void updatePlaygroundProfile(
      String address,
      String university,
      String major,
      String introduction,
      String skill,
      String mbti,
      String mbtiDescription,
      Double sojuCapacity,
      String interest,
      Boolean isPourSauceLover,
      Boolean isHardPeachLover,
      Boolean isMintChocoLover,
      Boolean isRedBeanFishBreadLover,
      Boolean isSojuLover,
      Boolean isRiceTteokLover,
      String idealType,
      String selfIntroduction,
      Boolean openToWork,
      Boolean openToSideProject,
      Boolean allowOfficial,
      Boolean editActivitiesAble,
      Boolean isPhoneBlind) {
    this.address = address;
    this.university = university;
    this.major = major;
    this.introduction = introduction;
    this.skill = skill;
    this.mbti = mbti;
    this.mbtiDescription = mbtiDescription;
    this.sojuCapacity = sojuCapacity;
    this.interest = interest;
    this.isPourSauceLover = isPourSauceLover;
    this.isHardPeachLover = isHardPeachLover;
    this.isMintChocoLover = isMintChocoLover;
    this.isRedBeanFishBreadLover = isRedBeanFishBreadLover;
    this.isSojuLover = isSojuLover;
    this.isRiceTteokLover = isRiceTteokLover;
    this.idealType = idealType;
    this.selfIntroduction = selfIntroduction;
    this.openToWork = openToWork;
    this.openToSideProject = openToSideProject;
    this.allowOfficial = allowOfficial;
    this.editActivitiesAble = editActivitiesAble;
    this.isPhoneBlind = isPhoneBlind;
  }

  public void updateAuthPlatform(String authPlatformId, OAuthPlatform authPlatformType) {
    this.authPlatformId = authPlatformId;
    this.authPlatformType = authPlatformType;
  }

  public void completeFirstLogin() {
    this.isFirstLogin = false;
  }

  public User toDomain() {
    return User.createUser(getId(), socialAccount(), toProfile(), isFirstLogin);
  }

  public User toDomainWithActivities(List<UserActivityHistoryEntity> activities) {
    ActivityList activityList =
        activities.isEmpty()
            ? new ActivityList()
            : ActivityList.of(
                activities.stream().map(UserActivityHistoryEntity::toDomain).toList());
    return User.createUser(getId(), socialAccount(), toProfile(), activityList, isFirstLogin);
  }

  private SocialAccount socialAccount() {
    return SocialAccount.of(authPlatformId, authPlatformType);
  }

  private Profile toProfile() {
    return Profile.ofFull(
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
        UserFavor.of(
            isPourSauceLover,
            isHardPeachLover,
            isMintChocoLover,
            isRedBeanFishBreadLover,
            isSojuLover,
            isRiceTteokLover),
        idealType,
        selfIntroduction,
        skill,
        openToWork,
        openToSideProject,
        allowOfficial,
        editActivitiesAble,
        isPhoneBlind,
        null, // workPreference: UserWorkPreferenceEntity에서 별도 조회
        List.of(), // links: UserLinkEntity에서 별도 조회
        List.of()); // careers: UserCareerEntity에서 별도 조회
  }

  public static UserEntity fromDomain(User user) {
    Profile profile = user.profile();
    SocialAccount socialAccount = user.socialAccount();
    UserEntity entity =
        UserEntity.builder()
            .name(profile.name())
            .phone(profile.phone())
            .email(profile.email())
            .birthday(profile.birthday())
            .profileImage(profile.profileImage())
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
