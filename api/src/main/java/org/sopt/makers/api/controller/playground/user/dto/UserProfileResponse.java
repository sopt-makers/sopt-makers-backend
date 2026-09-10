package org.sopt.makers.api.controller.playground.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Comparator;
import java.util.List;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;

public record UserProfileResponse(
    @Schema(required = true) Long id,
    @Schema(required = true) String name,
    String profileImage,
    String birthday,
    String phone,
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
    UserFavorResponse userFavor,
    String idealType,
    String selfIntroduction,
    @Schema(required = true) List<MemberSoptActivityResponse> activities,
    List<MemberLinkResponse> links,
    List<MemberCareerResponse> careers,
    @JsonInclude(JsonInclude.Include.NON_NULL) MemberAskPreviewResponse questionPreview,
    Boolean allowOfficial,
    Boolean isCoffeeChatActivate) {

  public record UserFavorResponse(
      Boolean isPourSauceLover,
      Boolean isHardPeachLover,
      Boolean isMintChocoLover,
      Boolean isRedBeanFishBreadLover,
      Boolean isSojuLover,
      Boolean isRiceTteokLover) {}

  public record MemberLinkResponse(Long id, String title, String url) {}

  public record MemberSoptActivityResponse(Long id, Integer generation, String part, String team) {}

  public record MemberCareerResponse(
      Long id,
      String companyName,
      String title,
      String startDate,
      String endDate,
      Boolean isCurrent) {}

  public record MemberAskPreviewResponse(Long questionId, String content) {}

  public static UserProfileResponse from(User user, boolean isCoffeeChatActivate) {
    return from(user, isCoffeeChatActivate, null);
  }

  public static UserProfileResponse from(
      User user, boolean isCoffeeChatActivate, MemberAskPreviewResponse questionPreview) {
    List<MemberSoptActivityResponse> activities =
        user.activities().activities().stream()
            .sorted(Comparator.comparingInt(Activity::generation).thenComparing(a -> !a.isSopt()))
            .map(
                a ->
                    new MemberSoptActivityResponse(
                        a.id(),
                        a.generation(),
                        a.part() == null ? null : a.part().getName(),
                        a.team() == null ? null : a.team().getDisplayName()))
            .toList();

    List<MemberLinkResponse> links =
        user.profile().links().stream()
            .map(l -> new MemberLinkResponse(l.id(), l.title(), l.url()))
            .toList();

    List<MemberCareerResponse> careers =
        user.profile().careers().stream()
            .map(
                c ->
                    new MemberCareerResponse(
                        c.id(),
                        c.companyName(),
                        c.title(),
                        c.startDate(),
                        c.endDate(),
                        c.isCurrent()))
            .toList();

    UserFavorResponse userFavor =
        user.profile().userFavor() == null
            ? null
            : new UserFavorResponse(
                user.profile().userFavor().isPourSauceLover(),
                user.profile().userFavor().isHardPeachLover(),
                user.profile().userFavor().isMintChocoLover(),
                user.profile().userFavor().isRedBeanFishBreadLover(),
                user.profile().userFavor().isSojuLover(),
                user.profile().userFavor().isRiceTteokLover());

    return new UserProfileResponse(
        user.id(),
        user.profile().name(),
        user.profile().profileImage(),
        user.profile().birthday() == null ? null : user.profile().birthday().toString(),
        user.profile().phone(),
        user.profile().email(),
        user.profile().address(),
        user.profile().university(),
        user.profile().major(),
        user.profile().introduction(),
        user.profile().skill(),
        user.profile().mbti(),
        user.profile().mbtiDescription(),
        user.profile().sojuCapacity(),
        user.profile().interest(),
        userFavor,
        user.profile().idealType(),
        user.profile().selfIntroduction(),
        activities,
        links,
        careers,
        questionPreview,
        user.profile().allowOfficial(),
        isCoffeeChatActivate);
  }
}
