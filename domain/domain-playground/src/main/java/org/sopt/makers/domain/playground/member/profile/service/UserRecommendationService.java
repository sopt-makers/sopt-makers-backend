package org.sopt.makers.domain.playground.member.profile.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.playground.member.ask.port.CurrentGenerationProvider;
import org.sopt.makers.domain.playground.member.profile.AppJamObMemberIds;
import org.sopt.makers.domain.playground.member.profile.RecommendationType;
import org.sopt.makers.domain.playground.member.profile.SameGenerationAndPartUser;
import org.sopt.makers.domain.playground.member.profile.UserRecommendation;
import org.sopt.makers.domain.playground.member.profile.WorkPreferenceRecommendationResult;
import org.sopt.makers.domain.playground.member.profile.WorkPreferenceRecommendationResult.WorkPreferenceRecommendedMember;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundCrewRelationPort;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundProjectRelationPort;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.sopt.makers.domain.user.port.PlaygroundRecommendationUserPort;
import org.sopt.makers.domain.user.port.PlaygroundRecommendationUserPort.RecommendationUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRecommendationService {

  private static final int RECOMMENDATION_SET_SIZE = 5;
  private static final int SAME_GENERATION_AND_PART_SET_SIZE = 4;
  private static final int WORK_PREFERENCE_MIN_MATCH_COUNT = 3;
  private static final int WORK_PREFERENCE_RECOMMENDATION_LIMIT = 4;

  private static final List<RecommendationType> ME_CRITERIA =
      List.of(
          RecommendationType.SAME_PART,
          RecommendationType.SAME_CREW,
          RecommendationType.SAME_MBTI,
          RecommendationType.SAME_UNIVERSITY,
          RecommendationType.SAME_GENERATION);

  private static final List<RecommendationType> USER_CRITERIA =
      List.of(
          RecommendationType.SAME_PART,
          RecommendationType.SAME_CREW,
          RecommendationType.SAME_PROJECT,
          RecommendationType.SAME_UNIVERSITY,
          RecommendationType.SAME_GENERATION);

  private final PlaygroundProfileUserPort playgroundProfileUserPort;
  private final PlaygroundRecommendationUserPort recommendationUserPort;
  private final PlaygroundCrewRelationPort crewRelationPort;
  private final PlaygroundProjectRelationPort projectRelationPort;
  private final CurrentGenerationProvider currentGenerationProvider;

  public List<UserRecommendation> getRecommendationsForMe(Long userId) {
    return buildRecommendations(userId, ME_CRITERIA);
  }

  public List<UserRecommendation> getRecommendationsForUser(Long userId) {
    return buildRecommendations(userId, USER_CRITERIA);
  }

  public List<SameGenerationAndPartUser> getSameGenerationAndPartRecommendations(Long userId) {
    User user = playgroundProfileUserPort.getUserWithActivities(userId);
    List<Activity> activities = user.activities().activities();

    Optional<Activity> latestSopt =
        activities.stream()
            .filter(Activity::isSopt)
            .max(Comparator.comparingInt(Activity::generation));
    Optional<Activity> latestMakers =
        activities.stream()
            .filter(activity -> !activity.isSopt())
            .max(Comparator.comparingInt(this::normalizedGeneration));

    if (latestMakers.isEmpty()) {
      return latestSopt
          .map(activity -> findSameGenerationAndPartSoptCandidates(userId, activity))
          .orElse(List.of());
    }

    int soptNormalized = latestSopt.map(this::normalizedGeneration).orElse(-1);
    int makersNormalized = normalizedGeneration(latestMakers.get());

    if (soptNormalized >= makersNormalized) {
      return findSameGenerationAndPartSoptCandidates(userId, latestSopt.get());
    }
    return findSameGenerationAndPartMakersCandidates(userId, latestMakers.get());
  }

  public WorkPreferenceRecommendationResult getWorkPreferenceRecommendations(Long userId) {
    RecommendationUserInfo me =
        recommendationUserPort.findRecommendationUserInfosByIds(List.of(userId)).stream()
            .findFirst()
            .orElse(null);
    if (me == null || me.workPreference() == null) {
      return new WorkPreferenceRecommendationResult(false, List.of());
    }

    int currentGeneration = currentGenerationProvider.getCurrentGeneration();
    int myLatestGeneration =
        me.activities().stream().mapToInt(Activity::generation).max().orElse(-1);
    boolean enableEvent =
        myLatestGeneration == currentGeneration || AppJamObMemberIds.IDS.contains(userId);
    if (!enableEvent) {
      return new WorkPreferenceRecommendationResult(true, List.of());
    }

    List<Long> candidateIds =
        recommendationUserPort.findUserIdsWithWorkPreference().stream()
            .filter(id -> !id.equals(userId))
            .toList();
    if (candidateIds.isEmpty()) {
      return new WorkPreferenceRecommendationResult(true, List.of());
    }

    List<RecommendationUserInfo> candidates =
        recommendationUserPort.findRecommendationUserInfosByIds(candidateIds);

    List<RecommendationUserInfo> matched =
        candidates.stream()
            .filter(candidate -> candidate.workPreference() != null)
            .filter(
                candidate ->
                    candidate.activities().stream()
                        .anyMatch(activity -> activity.generation() == currentGeneration))
            .filter(
                candidate ->
                    matchCount(me.workPreference(), candidate.workPreference())
                        >= WORK_PREFERENCE_MIN_MATCH_COUNT)
            .toList();

    List<RecommendationUserInfo> shuffled = new ArrayList<>(matched);
    Collections.shuffle(shuffled);

    List<WorkPreferenceRecommendedMember> recommendations =
        shuffled.stream()
            .limit(WORK_PREFERENCE_RECOMMENDATION_LIMIT)
            .map(candidate -> toWorkPreferenceRecommendedMember(candidate, currentGeneration))
            .toList();

    return new WorkPreferenceRecommendationResult(true, recommendations);
  }

  public PlaygroundCrewRelationPort.CrewMeetingPage getCrewMeetings(
      Long userId, int pageNo, int limit) {
    return crewRelationPort.findJoinedMeetings(userId, pageNo, limit);
  }

  /**
   * 레거시 InternalOpenApiController(POST /internal/api/v1/members/profile/recommend)의
   * InternalApiService#getMemberIdsByRecommendFilter를 대체한다. 세대(SOPT 기수) 필터에 해당하는 유저 중,
   * university/mbti 조건(둘 다 nullable)을 모두 만족하는 유저 id만 남긴다.
   *
   * <p>레거시는 university를 부분 일치(LIKE)로 비교했지만, 이 코드베이스의 다른 추천 기능(예: {@link
   * #findSameUniversityCandidate})과 동일하게 완전 일치로 비교한다.
   */
  public Set<Long> getRecommendedMemberIdsByGenerationAndFilter(
      List<Integer> generations, String university, String mbti) {
    Set<Long> generationUserIds = new HashSet<>();
    for (int generation : generations) {
      generationUserIds.addAll(
          recommendationUserPort.findUserIdsByActivity(generation, null, true));
    }

    boolean hasUniversity = university != null && !university.isBlank();
    boolean hasMbti = mbti != null && !mbti.isBlank();
    if (!hasUniversity && !hasMbti) {
      return generationUserIds;
    }

    Set<Long> filteredIds = resolveProfileFilterUserIds(university, mbti, hasUniversity, hasMbti);
    generationUserIds.retainAll(filteredIds);
    return generationUserIds;
  }

  private Set<Long> resolveProfileFilterUserIds(
      String university, String mbti, boolean hasUniversity, boolean hasMbti) {
    if (hasUniversity && hasMbti) {
      Set<Long> universityIds =
          new HashSet<>(recommendationUserPort.findUserIdsByUniversity(university));
      return recommendationUserPort.findUserIdsByMbti(mbti).stream()
          .filter(universityIds::contains)
          .collect(Collectors.toSet());
    }
    if (hasUniversity) {
      return new HashSet<>(recommendationUserPort.findUserIdsByUniversity(university));
    }
    return new HashSet<>(recommendationUserPort.findUserIdsByMbti(mbti));
  }

  private List<UserRecommendation> buildRecommendations(
      Long userId, List<RecommendationType> criteria) {
    User currentUser = playgroundProfileUserPort.getUserWithActivities(userId);
    List<Activity> myActivities = currentUser.activities().activities();

    Set<Part> myParts =
        myActivities.stream()
            .filter(Activity::isSopt)
            .map(Activity::part)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    int myLatestGeneration =
        myActivities.stream()
            .filter(Activity::isSopt)
            .mapToInt(Activity::generation)
            .max()
            .orElse(-1);
    String myMbti = currentUser.profile().mbti();
    String myUniversity = currentUser.profile().university();

    Set<Long> excludeIds = new HashSet<>();
    excludeIds.add(userId);

    List<UserRecommendation> recommendations = new ArrayList<>();
    int criteriaPointer = 0;
    for (int slot = 0;
        slot < RECOMMENDATION_SET_SIZE && criteriaPointer < criteria.size();
        slot++) {
      boolean isFound = false;
      while (criteriaPointer < criteria.size() && !isFound) {
        RecommendationType type = criteria.get(criteriaPointer++);
        Optional<UserRecommendation> result =
            switch (type) {
              case SAME_PART -> findSamePartCandidate(myParts, excludeIds);
              case SAME_CREW -> findSameCrewCandidate(userId, excludeIds);
              case SAME_MBTI -> findSameMbtiCandidate(myMbti, excludeIds);
              case SAME_PROJECT -> findSameProjectCandidate(userId, excludeIds);
              case SAME_UNIVERSITY -> findSameUniversityCandidate(myUniversity, excludeIds);
              case SAME_GENERATION -> findSameGenerationCandidate(myLatestGeneration, excludeIds);
            };
        if (result.isPresent()) {
          recommendations.add(result.get());
          excludeIds.add(result.get().id());
          isFound = true;
        }
      }
    }

    Collections.shuffle(recommendations);
    return recommendations;
  }

  private Optional<UserRecommendation> findSamePartCandidate(
      Set<Part> myParts, Set<Long> excludeIds) {
    if (myParts.isEmpty()) {
      return Optional.empty();
    }
    Set<Long> candidateIds = new LinkedHashSet<>();
    myParts.forEach(
        part ->
            candidateIds.addAll(recommendationUserPort.findUserIdsByActivity(null, part, true)));
    candidateIds.removeAll(excludeIds);
    return pickAndBuildFromIds(new ArrayList<>(candidateIds), RecommendationType.SAME_PART);
  }

  private Optional<UserRecommendation> findSameCrewCandidate(Long userId, Set<Long> excludeIds) {
    List<Long> relatedIds =
        crewRelationPort.findRelatedUserIds(userId).stream()
            .filter(id -> !excludeIds.contains(id))
            .distinct()
            .toList();
    if (relatedIds.isEmpty()) {
      return Optional.empty();
    }

    List<RecommendationUserInfo> infos =
        recommendationUserPort.findRecommendationUserInfosByIds(relatedIds);
    int currentGeneration = currentGenerationProvider.getCurrentGeneration();

    List<RecommendationUserInfo> currentGenerationCandidates =
        infos.stream()
            .filter(
                info ->
                    info.activities().stream()
                        .anyMatch(a -> a.isSopt() && a.generation() == currentGeneration))
            .toList();

    List<RecommendationUserInfo> pool =
        currentGenerationCandidates.isEmpty() ? infos : currentGenerationCandidates;
    return pickAndBuildFromInfos(pool, RecommendationType.SAME_CREW);
  }

  private Optional<UserRecommendation> findSameMbtiCandidate(String mbti, Set<Long> excludeIds) {
    if (mbti == null || mbti.isBlank()) {
      return Optional.empty();
    }
    List<Long> candidates =
        recommendationUserPort.findUserIdsByMbti(mbti).stream()
            .filter(id -> !excludeIds.contains(id))
            .toList();
    return pickAndBuildFromIds(candidates, RecommendationType.SAME_MBTI);
  }

  private Optional<UserRecommendation> findSameProjectCandidate(Long userId, Set<Long> excludeIds) {
    List<Project> myProjects = projectRelationPort.findProjectsByUserId(userId);
    if (myProjects.isEmpty()) {
      return Optional.empty();
    }
    List<Long> projectIds = myProjects.stream().map(Project::id).toList();
    List<Long> candidates =
        projectRelationPort.findUserIdsByProjectIds(projectIds).stream()
            .filter(id -> !excludeIds.contains(id))
            .toList();
    return pickAndBuildFromIds(candidates, RecommendationType.SAME_PROJECT);
  }

  private Optional<UserRecommendation> findSameUniversityCandidate(
      String university, Set<Long> excludeIds) {
    if (university == null || university.isBlank()) {
      return Optional.empty();
    }
    List<Long> candidates =
        recommendationUserPort.findUserIdsByUniversity(university).stream()
            .filter(id -> !excludeIds.contains(id))
            .toList();
    return pickAndBuildFromIds(candidates, RecommendationType.SAME_UNIVERSITY);
  }

  private Optional<UserRecommendation> findSameGenerationCandidate(
      int myLatestGeneration, Set<Long> excludeIds) {
    if (myLatestGeneration < 0) {
      return Optional.empty();
    }
    List<Long> candidates =
        recommendationUserPort.findUserIdsByActivity(myLatestGeneration, null, true).stream()
            .filter(id -> !excludeIds.contains(id))
            .toList();
    return pickAndBuildFromIds(candidates, RecommendationType.SAME_GENERATION);
  }

  private Optional<UserRecommendation> pickAndBuildFromIds(
      List<Long> candidateIds, RecommendationType type) {
    if (candidateIds.isEmpty()) {
      return Optional.empty();
    }
    List<Long> shuffled = new ArrayList<>(candidateIds);
    Collections.shuffle(shuffled);
    Long picked = shuffled.get(0);
    List<RecommendationUserInfo> infos =
        recommendationUserPort.findRecommendationUserInfosByIds(List.of(picked));
    if (infos.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(toRecommendation(infos.get(0), type));
  }

  private Optional<UserRecommendation> pickAndBuildFromInfos(
      List<RecommendationUserInfo> infos, RecommendationType type) {
    if (infos.isEmpty()) {
      return Optional.empty();
    }
    List<RecommendationUserInfo> shuffled = new ArrayList<>(infos);
    Collections.shuffle(shuffled);
    return Optional.of(toRecommendation(shuffled.get(0), type));
  }

  private UserRecommendation toRecommendation(
      RecommendationUserInfo info, RecommendationType type) {
    Activity latest =
        info.activities().stream().max(Comparator.comparingInt(Activity::generation)).orElse(null);
    Activity latestSopt =
        info.activities().stream()
            .filter(Activity::isSopt)
            .max(Comparator.comparingInt(Activity::generation))
            .orElse(null);

    Integer generation = latest != null ? latest.generation() : null;
    String part;
    if (latest != null && !latest.isSopt()) {
      part = "메이커스";
    } else {
      part = latestSopt != null && latestSopt.part() != null ? latestSopt.part().getName() : null;
    }

    return new UserRecommendation(
        info.id(), info.name(), info.profileImage(), generation, part, type);
  }

  private List<SameGenerationAndPartUser> findSameGenerationAndPartSoptCandidates(
      Long excludeId, Activity latestSopt) {
    if (latestSopt.part() == null) {
      return List.of();
    }
    List<Long> candidates =
        recommendationUserPort
            .findUserIdsByActivity(latestSopt.generation(), latestSopt.part(), true)
            .stream()
            .filter(id -> !id.equals(excludeId))
            .toList();
    return pickTopSameGenerationAndPartMembers(
        candidates, latestSopt.generation(), latestSopt.part().getName());
  }

  private List<SameGenerationAndPartUser> findSameGenerationAndPartMakersCandidates(
      Long excludeId, Activity latestMakers) {
    List<Long> candidates =
        recommendationUserPort
            .findUserIdsByActivity(latestMakers.generation(), null, false)
            .stream()
            .filter(id -> !id.equals(excludeId))
            .toList();
    return pickTopSameGenerationAndPartMembers(candidates, latestMakers.generation(), "메이커스");
  }

  private List<SameGenerationAndPartUser> pickTopSameGenerationAndPartMembers(
      List<Long> candidateIds, int generation, String part) {
    List<Long> shuffled = new ArrayList<>(candidateIds);
    Collections.shuffle(shuffled);
    List<Long> picked = shuffled.stream().limit(SAME_GENERATION_AND_PART_SET_SIZE).toList();
    if (picked.isEmpty()) {
      return List.of();
    }

    Map<Long, RecommendationUserInfo> infoById =
        recommendationUserPort.findRecommendationUserInfosByIds(picked).stream()
            .collect(Collectors.toMap(RecommendationUserInfo::id, Function.identity()));

    return picked.stream()
        .map(infoById::get)
        .filter(Objects::nonNull)
        .map(
            info ->
                new SameGenerationAndPartUser(
                    info.id(), info.name(), info.profileImage(), generation, part))
        .toList();
  }

  private int normalizedGeneration(Activity activity) {
    if (!activity.isSopt() && activity.generation() >= 1 && activity.generation() <= 4) {
      return activity.generation() + 30;
    }
    return activity.generation();
  }

  private int matchCount(WorkPreference current, WorkPreference candidate) {
    int count = 0;
    if (current.ideationStyle() == candidate.ideationStyle()) count++;
    if (current.workTime() == candidate.workTime()) count++;
    if (current.communicationStyle() == candidate.communicationStyle()) count++;
    if (current.workPlace() == candidate.workPlace()) count++;
    if (current.feedbackStyle() == candidate.feedbackStyle()) count++;
    return count;
  }

  private WorkPreferenceRecommendedMember toWorkPreferenceRecommendedMember(
      RecommendationUserInfo info, int currentGeneration) {
    Activity currentGenerationActivity =
        info.activities().stream()
            .filter(activity -> activity.generation() == currentGeneration)
            .findFirst()
            .orElse(null);
    return new WorkPreferenceRecommendedMember(
        info.id(),
        info.name(),
        info.profileImage(),
        info.university(),
        currentGenerationActivity,
        info.workPreference());
  }
}
