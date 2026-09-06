package org.sopt.makers.api.controller.playground.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sopt.makers.domain.playground.member.profile.MemberProfileDetail;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.WorkPreference;

public record UserProfileSpecificResponse(
    @Schema(required = true) String name,
    String profileImage,
    LocalDate birthday,
    Boolean isPhoneBlind,
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
    WorkPreferenceResponse workPreference,
    @Schema(required = true) List<MemberActivityResponse> activities,
    @Schema(required = true) List<SoptMemberActivityResponse> soptActivities,
    List<MemberLinkResponse> links,
    List<MemberProjectResponse> projects,
    List<MemberCareerResponse> careers,
    Boolean allowOfficial,
    Boolean hasRecentQuestion,
    Boolean isCoffeeChatActivate,
    @Schema(required = true) Boolean isMine) {

  public record MemberLinkResponse(Long id, String title, String url) {}

  public record UserFavorResponse(
      Boolean isPourSauceLover,
      Boolean isHardPeachLover,
      Boolean isMintChocoLover,
      Boolean isRedBeanFishBreadLover,
      Boolean isSojuLover,
      Boolean isRiceTteokLover) {}

  public record WorkPreferenceResponse(
      String ideationStyle, String workTime, String communicationStyle, String workPlace, String feedbackStyle) {}

  public record MemberProjectResponse(
      Long id,
      String name,
      String summary,
      Integer generation,
      String category,
      String logoImage,
      String thumbnailImage,
      List<String> serviceType) {}

  public record MemberActivityResponse(String cardinalInfo, List<ActivityVo> cardinalActivities) {}

  public record SoptMemberActivityResponse(
      Integer generation, String part, String team, List<MemberProjectVo> projects) {}

  public record MemberCareerResponse(
      Long id, String companyName, String title, String startDate, String endDate, Boolean isCurrent) {}

  public record ActivityVo(Long id, Integer generation, String team, String part, boolean isProject) {}

  public record MemberProjectVo(Long id, Integer generation, String name, String category) {}

  public static UserProfileSpecificResponse from(MemberProfileDetail detail) {
    User user = detail.user();
    List<Activity> sortedActivities =
        user.activities().activities().stream()
            .sorted(Comparator.comparingInt(Activity::generation).thenComparing(a -> !a.isSopt()))
            .toList();
    List<Project> projects = detail.projects();

    Map<Integer, String> cardinalInfoMap = new LinkedHashMap<>();
    for (Activity activity : sortedActivities) {
      cardinalInfoMap.putIfAbsent(
          activity.generation(), activity.part() == null ? "" : activity.part().getName());
    }

    List<MemberActivityResponse> activities = buildActivities(sortedActivities, projects, cardinalInfoMap);
    List<SoptMemberActivityResponse> soptActivities = buildSoptActivities(sortedActivities, projects);
    List<MemberProjectResponse> flatProjects = buildFlatProjects(projects);
    List<MemberCareerResponse> careers = buildCareers(user);

    boolean isPhoneBlind = Boolean.TRUE.equals(user.profile().isPhoneBlind());
    String phone = (detail.isMine() || !isPhoneBlind) ? user.profile().phone() : null;

    return new UserProfileSpecificResponse(
        user.profile().name(),
        user.profile().profileImage(),
        user.profile().birthday(),
        user.profile().isPhoneBlind(),
        phone,
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
        toUserFavorResponse(user),
        user.profile().idealType(),
        user.profile().selfIntroduction(),
        toWorkPreferenceResponse(user.profile().workPreference()),
        activities,
        soptActivities,
        toLinks(user),
        flatProjects,
        careers,
        user.profile().allowOfficial(),
        detail.hasRecentQuestion(),
        detail.isCoffeeChatActivate(),
        detail.isMine());
  }

  private static List<MemberActivityResponse> buildActivities(
      List<Activity> sortedActivities, List<Project> projects, Map<Integer, String> cardinalInfoMap) {
    Stream<ActivityVo> activityVos =
        sortedActivities.stream()
            .map(
                a ->
                    new ActivityVo(
                        a.id(),
                        a.generation(),
                        a.team() == null ? null : a.team().getDisplayName(),
                        a.part() == null ? null : a.part().getName(),
                        false));

    Stream<ActivityVo> projectVos =
        projects.stream()
            .filter(p -> p.generation() != null)
            .map(
                p ->
                    new ActivityVo(
                        p.id(),
                        p.generation(),
                        p.name(),
                        cardinalInfoMap.getOrDefault(p.generation(), ""),
                        true));

    Map<Integer, List<ActivityVo>> genActivityMap =
        Stream.concat(activityVos, projectVos).collect(Collectors.groupingBy(ActivityVo::generation));

    Map<String, List<ActivityVo>> result = new LinkedHashMap<>();
    sortedActivities.stream()
        .map(Activity::generation)
        .distinct()
        .forEach(
            gen -> {
              List<ActivityVo> genActivities = genActivityMap.get(gen);
              if (genActivities != null) {
                result.put(gen + "," + cardinalInfoMap.getOrDefault(gen, ""), genActivities);
              }
            });

    return result.entrySet().stream()
        .map(entry -> new MemberActivityResponse(entry.getKey(), entry.getValue()))
        .toList();
  }

  private static List<SoptMemberActivityResponse> buildSoptActivities(
      List<Activity> sortedActivities, List<Project> projects) {
    return sortedActivities.stream()
        .map(
            activity -> {
              List<MemberProjectVo> activityProjects =
                  projects.stream()
                      .filter(p -> p.generation() != null && p.generation().equals(activity.generation()))
                      .map(p -> new MemberProjectVo(p.id(), p.generation(), p.name(), p.category()))
                      .toList();
              return new SoptMemberActivityResponse(
                  activity.generation(),
                  activity.part() == null ? null : activity.part().getName(),
                  activity.team() == null ? null : activity.team().getDisplayName(),
                  activityProjects);
            })
        .toList();
  }

  private static List<MemberProjectResponse> buildFlatProjects(List<Project> projects) {
    return projects.stream()
        .map(
            p ->
                new MemberProjectResponse(
                    p.id(),
                    p.name(),
                    p.summary(),
                    p.generation(),
                    p.category(),
                    p.logoImage(),
                    p.thumbnailImage(),
                    p.serviceType()))
        .toList();
  }

  private static List<MemberCareerResponse> buildCareers(User user) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    List<MemberCareerResponse> careers =
        new ArrayList<>(
            user.profile().careers().stream()
                .map(
                    c ->
                        new MemberCareerResponse(
                            c.id(), c.companyName(), c.title(), c.startDate(), c.endDate(), c.isCurrent()))
                .toList());

    careers.sort(
        (a, b) -> {
          YearMonth start = YearMonth.parse(a.startDate(), formatter);
          YearMonth end = YearMonth.parse(b.startDate(), formatter);
          return end.compareTo(start);
        });

    MemberCareerResponse currentCareer = null;
    int index = 0;
    for (MemberCareerResponse career : careers) {
      if (Boolean.TRUE.equals(career.isCurrent())) {
        currentCareer = career;
        break;
      }
      index += 1;
    }
    if (currentCareer != null) {
      careers.add(0, currentCareer);
      careers.remove(index + 1);
    }
    return careers;
  }

  private static List<MemberLinkResponse> toLinks(User user) {
    return user.profile().links().stream().map(l -> new MemberLinkResponse(l.id(), l.title(), l.url())).toList();
  }

  private static UserFavorResponse toUserFavorResponse(User user) {
    if (user.profile().userFavor() == null) {
      return null;
    }
    return new UserFavorResponse(
        user.profile().userFavor().isPourSauceLover(),
        user.profile().userFavor().isHardPeachLover(),
        user.profile().userFavor().isMintChocoLover(),
        user.profile().userFavor().isRedBeanFishBreadLover(),
        user.profile().userFavor().isSojuLover(),
        user.profile().userFavor().isRiceTteokLover());
  }

  private static WorkPreferenceResponse toWorkPreferenceResponse(WorkPreference workPreference) {
    if (workPreference == null) {
      return null;
    }
    return new WorkPreferenceResponse(
        workPreference.ideationStyle() == null ? null : workPreference.ideationStyle().getValue(),
        workPreference.workTime() == null ? null : workPreference.workTime().getValue(),
        workPreference.communicationStyle() == null ? null : workPreference.communicationStyle().getValue(),
        workPreference.workPlace() == null ? null : workPreference.workPlace().getValue(),
        workPreference.feedbackStyle() == null ? null : workPreference.feedbackStyle().getValue());
  }
}
