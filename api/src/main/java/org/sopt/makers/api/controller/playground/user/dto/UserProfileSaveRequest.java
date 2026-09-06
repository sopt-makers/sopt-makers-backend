package org.sopt.makers.api.controller.playground.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

public record UserProfileSaveRequest(
    @Schema(required = true) String name,
    String profileImage,
    LocalDate birthday,
    // 레거시 Constant.PHONE_NUMBER_REGEX와 동일한 패턴('-' 제외 11자리 숫자)
    @Pattern(regexp = "^\\d{11}$", message = "잘못된 전화번호 형식입니다. '-'을 제외한 11자의 번호를 입력해주세요.") String phone,
    String email,
    String address,
    String university,
    String major,
    String introduction,
    String skill,
    String mbti,
    String mbtiDescription,
    Double sojuCapacity,
    String interest,
    UserFavorRequest userFavor,
    String idealType,
    String selfIntroduction,
    List<MemberLinkSaveRequest> links,
    @Schema(required = true) List<MemberSoptActivitySaveRequest> activities,
    List<MemberCareerSaveRequest> careers,
    Boolean allowOfficial,
    Boolean isPhoneBlind) {

  public record UserFavorRequest(
      Boolean isPourSauceLover,
      Boolean isHardPeachLover,
      Boolean isMintChocoLover,
      Boolean isRedBeanFishBreadLover,
      Boolean isSojuLover,
      Boolean isRiceTteokLover) {}

  public record MemberLinkSaveRequest(String title, String url) {}

  public record MemberSoptActivitySaveRequest(Integer generation, String part, String team) {}

  public record MemberCareerSaveRequest(
      String companyName,
      String title,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM") String startDate,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM") String endDate,
      Boolean isCurrent) {}
}
