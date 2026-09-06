package org.sopt.makers.domain.playground.member.ask.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousNicknameRetriever;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileImageRetriever;
import org.sopt.makers.domain.playground.member.ask.AskAnswerDetail;
import org.sopt.makers.domain.playground.member.ask.AskPreview;
import org.sopt.makers.domain.playground.member.ask.AskDetail;
import org.sopt.makers.domain.playground.member.ask.AskLocation;
import org.sopt.makers.domain.playground.member.ask.AskPage;
import org.sopt.makers.domain.playground.member.ask.AskTargetMember;
import org.sopt.makers.domain.playground.member.ask.LatestAnsweredAskCard;
import org.sopt.makers.domain.playground.member.ask.MyLatestAnsweredAskLocation;
import org.sopt.makers.domain.playground.member.ask.QuestionTab;
import org.sopt.makers.domain.playground.member.ask.UserAnswer;
import org.sopt.makers.domain.playground.member.ask.UserAsk;
import org.sopt.makers.domain.playground.member.ask.exception.UserAskException;
import org.sopt.makers.domain.playground.member.ask.exception.UserAskFailure;
import org.sopt.makers.domain.playground.member.ask.port.AnswerReactionRepositoryPort;
import org.sopt.makers.domain.playground.member.ask.port.AskMemberDirectoryPort;
import org.sopt.makers.domain.playground.member.ask.port.AskReactionRepositoryPort;
import org.sopt.makers.domain.playground.member.ask.port.UserAnswerRepositoryPort;
import org.sopt.makers.domain.playground.member.ask.port.UserAskRepositoryPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.port.PlaygroundAskUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 에스크 질문/답변 목록·위치 조회 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAskQueryService {

  private static final int MIN_PAGE_SIZE = 1;
  private static final int MAX_PAGE_SIZE = 100;
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final int LOCATION_PAGE_SIZE = 10;
  private static final int NEW_ASK_DAYS = 7;
  private static final int LATEST_CARD_COUNT = 5;
  private static final int LATEST_FETCH_SIZE = 50;
  private static final int RECENT_ASK_DAYS = 7;
  private static final int QUESTION_PREVIEW_DAYS = 7;

  private static final DateTimeFormatter CAREER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

  private final UserAskRepositoryPort userAskRepositoryPort;
  private final UserAnswerRepositoryPort userAnswerRepositoryPort;
  private final AskReactionRepositoryPort askReactionRepositoryPort;
  private final AnswerReactionRepositoryPort answerReactionRepositoryPort;
  private final PlaygroundAskUserPort playgroundAskUserPort;
  private final AnonymousNicknameRetriever anonymousNicknameRetriever;
  private final AnonymousProfileImageRetriever anonymousProfileImageRetriever;
  private final AskMemberDirectoryPort askMemberDirectoryPort;

  public AskPage getAsks(Long currentUserId, Long receiverUserId, QuestionTab tab, Integer page, Integer size) {
    int pageNumber = page != null ? page : 0;
    int pageSize = normalizePageSize(size);

    List<UserAsk> asks;
    long totalElements;

    if (tab == null) {
      asks = userAskRepositoryPort.findAllByReceiverUserId(receiverUserId, pageNumber, pageSize);
      totalElements = userAskRepositoryPort.countAllByReceiverUserId(receiverUserId);
    } else if (tab == QuestionTab.ANSWERED) {
      asks = userAskRepositoryPort.findAnsweredByReceiverUserId(receiverUserId, pageNumber, pageSize);
      totalElements = userAskRepositoryPort.countAnsweredByReceiverUserId(receiverUserId);
    } else {
      asks = userAskRepositoryPort.findUnansweredByReceiverUserId(receiverUserId, pageNumber, pageSize);
      totalElements = userAskRepositoryPort.countUnansweredByReceiverUserId(receiverUserId);
    }

    List<AskDetail> askDetails = buildAskDetails(asks, currentUserId);

    int totalPages = (int) Math.ceil((double) totalElements / pageSize);
    boolean hasNext = pageNumber < totalPages - 1;
    boolean hasPrevious = pageNumber > 0;

    return new AskPage(askDetails, pageNumber, pageSize, totalElements, totalPages, hasNext, hasPrevious);
  }

  public long getUnansweredCount(Long userId) {
    return userAskRepositoryPort.countUnansweredByReceiverUserId(userId);
  }

  /** 질문 가능 대상 멤버를 파트별로 큐레이션된 목록에서 조회한다. partName이 인식되지 않으면 전체 파트를 반환한다. */
  public List<AskTargetMember> getAskTargetMembers(String partName) {
    Part part = resolvePart(partName);
    List<Long> memberIds = askMemberDirectoryPort.getAskMemberIds(part);
    if (memberIds.isEmpty()) {
      return List.of();
    }

    List<User> users = playgroundAskUserPort.findAllWithActivitiesByIds(memberIds);

    List<AskTargetMember> targets = new ArrayList<>();
    for (User user : users) {
      if (user.isFirstLogin()) {
        continue;
      }
      Activity latestActivity = findLatestActivity(user);
      if (latestActivity == null) {
        continue;
      }
      UserCareer career = resolveCareer(user.profile().careers());
      targets.add(new AskTargetMember(user, latestActivity, career));
    }
    return targets;
  }

  private Part resolvePart(String partName) {
    if (partName == null || partName.isBlank()) {
      return null;
    }
    return switch (partName.toUpperCase()) {
      case "서버", "SERVER" -> Part.SERVER;
      case "IOS" -> Part.IOS;
      case "안드로이드", "ANDROID" -> Part.ANDROID;
      case "웹", "WEB" -> Part.WEB;
      case "디자인", "DESIGN" -> Part.DESIGN;
      case "기획", "PLAN" -> Part.PLAN;
      default -> null;
    };
  }

  private Activity findLatestActivity(User user) {
    return user.activities().activities().stream()
        .max(Comparator.comparingInt(Activity::generation).thenComparing(Activity::isSopt))
        .orElse(null);
  }

  private UserCareer resolveCareer(List<UserCareer> careers) {
    if (careers == null || careers.isEmpty()) {
      return null;
    }

    Optional<UserCareer> currentCareer =
        careers.stream().filter(c -> Boolean.TRUE.equals(c.isCurrent())).findFirst();
    if (currentCareer.isPresent()) {
      return currentCareer.get();
    }

    return careers.stream()
        .filter(c -> c.endDate() != null)
        .max(
            (c1, c2) -> {
              try {
                YearMonth end1 = YearMonth.parse(c1.endDate(), CAREER_DATE_FORMATTER);
                YearMonth end2 = YearMonth.parse(c2.endDate(), CAREER_DATE_FORMATTER);
                return end1.compareTo(end2);
              } catch (Exception e) {
                try {
                  YearMonth start1 = YearMonth.parse(c1.startDate(), CAREER_DATE_FORMATTER);
                  YearMonth start2 = YearMonth.parse(c2.startDate(), CAREER_DATE_FORMATTER);
                  return start1.compareTo(start2);
                } catch (Exception ex) {
                  return 0;
                }
              }
            })
        .orElse(careers.get(0));
  }

  public boolean hasRecentAsk(Long receiverUserId) {
    return userAskRepositoryPort.existsByReceiverUserIdAndCreatedAtAfter(
        receiverUserId, LocalDateTime.now().minusDays(RECENT_ASK_DAYS));
  }

  /** 페이지(최대 50명 등)에 한해 최근 7일 이내 미신고 질문 미리보기를 receiverUserId별로 최대 1건씩 조회한다. */
  public Map<Long, AskPreview> findRecentAskPreviews(List<Long> receiverUserIds) {
    if (receiverUserIds == null || receiverUserIds.isEmpty()) {
      return Map.of();
    }
    LocalDateTime since = LocalDateTime.now().minusDays(QUESTION_PREVIEW_DAYS);
    return userAskRepositoryPort.findLatestRecentByReceiverUserIds(receiverUserIds, since).stream()
        .collect(Collectors.toMap(UserAsk::receiverUserId, ask -> new AskPreview(ask.id(), ask.content())));
  }

  public AskLocation getAskLocation(Long receiverUserId, Long questionId) {
    UserAsk ask =
        userAskRepositoryPort.findById(questionId).orElseThrow(() -> new UserAskException(UserAskFailure.NOT_FOUND_ASK));

    if (!Objects.equals(ask.receiverUserId(), receiverUserId)) {
      throw new UserAskException(UserAskFailure.ASK_NOT_BELONG_TO_MEMBER);
    }

    UserAnswer answer = userAnswerRepositoryPort.findByQuestionId(questionId).orElse(null);
    return calculateLocation(ask, answer);
  }

  public MyLatestAnsweredAskLocation getMyLatestAnsweredAskLocation(Long userId, Long receiverUserId) {
    if (Objects.equals(userId, receiverUserId)) {
      throw new UserAskException(UserAskFailure.SELF_ASK_LOCATION_NOT_ALLOWED);
    }

    List<UserAsk> myAnsweredAsks =
        userAskRepositoryPort.findAllAnsweredByAskerUserIdAndReceiverUserIdOrderByLatest(userId, receiverUserId);

    if (myAnsweredAsks.isEmpty()) {
      return new MyLatestAnsweredAskLocation(null, null, null);
    }

    UserAsk latestAsk = myAnsweredAsks.get(0);
    List<Long> allAnsweredIds = userAskRepositoryPort.findAllAnsweredIdsByReceiverUserIdOrderByLatest(receiverUserId);

    int targetIndexInTotal = allAnsweredIds.indexOf(latestAsk.id());
    if (targetIndexInTotal == -1) {
      return new MyLatestAnsweredAskLocation(null, null, null);
    }

    int page = targetIndexInTotal / LOCATION_PAGE_SIZE;
    int index = targetIndexInTotal % LOCATION_PAGE_SIZE;

    return new MyLatestAnsweredAskLocation(latestAsk.id(), page, index);
  }

  public List<LatestAnsweredAskCard> getLatestAnsweredAsks() {
    List<UserAsk> recentAsks = userAskRepositoryPort.findLatestAnswered(LATEST_FETCH_SIZE);

    if (recentAsks.isEmpty()) {
      return List.of();
    }

    List<UserAsk> selectedAsks = selectLatestAsksForCards(recentAsks);

    List<Long> questionIds = selectedAsks.stream().map(UserAsk::id).toList();
    Map<Long, UserAnswer> answersByQuestionId =
        userAnswerRepositoryPort.findAllByQuestionIds(questionIds).stream()
            .collect(Collectors.toMap(UserAnswer::questionId, Function.identity()));

    List<Long> receiverIds = selectedAsks.stream().map(UserAsk::receiverUserId).distinct().toList();
    Map<Long, User> receiverInfoMap = indexById(playgroundAskUserPort.findAllWithActivitiesByIds(receiverIds));

    return selectedAsks.stream()
        .map(
            ask -> {
              User receiverInfo = receiverInfoMap.get(ask.receiverUserId());
              AskLocation location = calculateLocation(ask, answersByQuestionId.get(ask.id()));
              return new LatestAnsweredAskCard(
                  ask.receiverUserId(),
                  receiverInfo != null ? receiverInfo.profile().name() : null,
                  receiverInfo != null ? receiverInfo.profile().profileImage() : null,
                  ask.id(),
                  ask.content(),
                  location);
            })
        .toList();
  }

  private List<AskDetail> buildAskDetails(List<UserAsk> asks, Long currentUserId) {
    if (asks.isEmpty()) {
      return List.of();
    }

    List<Long> questionIds = asks.stream().map(UserAsk::id).toList();

    Map<Long, UserAnswer> answersByQuestionId =
        userAnswerRepositoryPort.findAllByQuestionIds(questionIds).stream()
            .collect(Collectors.toMap(UserAnswer::questionId, Function.identity()));
    List<Long> answerIds = answersByQuestionId.values().stream().map(UserAnswer::id).toList();

    Map<Long, Long> askReactionCounts = askReactionRepositoryPort.countGroupedByQuestionIds(questionIds);
    Set<Long> reactedQuestionIds = askReactionRepositoryPort.findReactedQuestionIdsByUser(questionIds, currentUserId);
    Map<Long, Long> answerReactionCounts = answerReactionRepositoryPort.countGroupedByAnswerIds(answerIds);
    Set<Long> reactedAnswerIds = answerReactionRepositoryPort.findReactedAnswerIdsByUser(answerIds, currentUserId);

    Set<Long> userIds = new HashSet<>();
    asks.forEach(
        ask -> {
          if (ask.askerUserId() != null) {
            userIds.add(ask.askerUserId());
          }
          userIds.add(ask.receiverUserId());
        });
    Map<Long, User> userInfoMap =
        indexById(playgroundAskUserPort.findAllWithActivitiesByIds(new ArrayList<>(userIds)));

    List<Long> anonymousNicknameIds =
        asks.stream()
            .filter(ask -> Boolean.TRUE.equals(ask.isAnonymous()))
            .map(UserAsk::anonymousNicknameId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    Map<Long, AnonymousNickname> anonymousNicknameMap = anonymousNicknameRetriever.findAllByIdsAsMap(anonymousNicknameIds);

    List<Long> anonymousProfileImageIds =
        asks.stream()
            .filter(ask -> Boolean.TRUE.equals(ask.isAnonymous()))
            .map(UserAsk::anonymousProfileImageId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    Map<Long, AnonymousProfileImage> anonymousProfileImageMap =
        anonymousProfileImageRetriever.getByIds(anonymousProfileImageIds);

    return asks.stream()
        .map(
            ask ->
                toAskDetail(
                    ask,
                    currentUserId,
                    answersByQuestionId.get(ask.id()),
                    askReactionCounts,
                    reactedQuestionIds,
                    answerReactionCounts,
                    reactedAnswerIds,
                    userInfoMap,
                    anonymousNicknameMap,
                    anonymousProfileImageMap))
        .toList();
  }

  private AskDetail toAskDetail(
      UserAsk ask,
      Long currentUserId,
      UserAnswer answer,
      Map<Long, Long> askReactionCounts,
      Set<Long> reactedQuestionIds,
      Map<Long, Long> answerReactionCounts,
      Set<Long> reactedAnswerIds,
      Map<Long, User> userInfoMap,
      Map<Long, AnonymousNickname> anonymousNicknameMap,
      Map<Long, AnonymousProfileImage> anonymousProfileImageMap) {

    boolean isAnonymous = Boolean.TRUE.equals(ask.isAnonymous());
    User askerInfo = userInfoMap.get(ask.askerUserId());

    Long askerId = isAnonymous ? null : ask.askerUserId();
    String askerName = isAnonymous ? null : (askerInfo != null ? askerInfo.profile().name() : null);
    String askerProfileImage = isAnonymous ? null : (askerInfo != null ? askerInfo.profile().profileImage() : null);
    String askerLatestGenerationLabel = askerInfo != null ? formatLatestGenerationLabel(askerInfo) : null;

    String anonymousNickname = null;
    String anonymousProfileImageUrl = null;
    if (isAnonymous && ask.anonymousNicknameId() != null) {
      AnonymousNickname nickname = anonymousNicknameMap.get(ask.anonymousNicknameId());
      anonymousNickname = nickname != null ? nickname.nickname() : null;
      if (ask.anonymousProfileImageId() != null) {
        AnonymousProfileImage profileImage = anonymousProfileImageMap.get(ask.anonymousProfileImageId());
        anonymousProfileImageUrl = profileImage != null ? profileImage.imageUrl() : null;
      }
    }

    long reactionCount = askReactionCounts.getOrDefault(ask.id(), 0L);
    boolean isReacted = reactedQuestionIds.contains(ask.id());

    AskAnswerDetail answerDetail = null;
    if (answer != null) {
      User receiverInfo = userInfoMap.get(ask.receiverUserId());
      long answerReactionCount = answerReactionCounts.getOrDefault(answer.id(), 0L);
      boolean isAnswerReacted = reactedAnswerIds.contains(answer.id());
      answerDetail =
          new AskAnswerDetail(
              answer.id(),
              answer.content(),
              answerReactionCount,
              isAnswerReacted,
              ask.receiverUserId(),
              receiverInfo != null ? receiverInfo.profile().name() : null,
              receiverInfo != null ? receiverInfo.profile().profileImage() : null,
              answer.createdAt());
    }

    boolean isNew = ask.createdAt().isAfter(LocalDateTime.now().minusDays(NEW_ASK_DAYS));
    boolean isMine = Objects.equals(ask.askerUserId(), currentUserId);
    boolean isReceived = Objects.equals(ask.receiverUserId(), currentUserId);

    return new AskDetail(
        ask.id(),
        ask.content(),
        askerId,
        askerName,
        askerProfileImage,
        askerLatestGenerationLabel,
        anonymousNickname,
        anonymousProfileImageUrl,
        ask.isAnonymous(),
        reactionCount,
        isReacted,
        answer != null,
        answerDetail,
        ask.createdAt(),
        isNew,
        isMine,
        isReceived);
  }

  private AskLocation calculateLocation(UserAsk ask, UserAnswer answer) {
    QuestionTab tab;
    long precedingCount;

    if (answer != null) {
      tab = QuestionTab.ANSWERED;
      precedingCount =
          userAskRepositoryPort.countAnsweredBeforeTargetInLatestOrder(
              ask.receiverUserId(), answer.createdAt(), ask.id());
    } else {
      tab = QuestionTab.UNANSWERED;
      precedingCount =
          userAskRepositoryPort.countUnansweredBeforeTargetInLatestOrder(
              ask.receiverUserId(), ask.createdAt(), ask.id());
    }

    int page = (int) (precedingCount / LOCATION_PAGE_SIZE);
    int index = (int) (precedingCount % LOCATION_PAGE_SIZE);

    return new AskLocation(ask.id(), tab, page, index);
  }

  private List<UserAsk> selectLatestAsksForCards(List<UserAsk> recentAsks) {
    List<UserAsk> selectedAsks = new ArrayList<>();
    Set<Long> selectedAskIds = new HashSet<>();
    Set<Long> selectedReceiverIds = new HashSet<>();

    for (UserAsk ask : recentAsks) {
      if (selectedReceiverIds.contains(ask.receiverUserId())) {
        continue;
      }
      selectedAsks.add(ask);
      selectedAskIds.add(ask.id());
      selectedReceiverIds.add(ask.receiverUserId());

      if (selectedAsks.size() == LATEST_CARD_COUNT) {
        return selectedAsks;
      }
    }

    for (UserAsk ask : recentAsks) {
      if (selectedAskIds.contains(ask.id())) {
        continue;
      }
      selectedAsks.add(ask);
      selectedAskIds.add(ask.id());

      if (selectedAsks.size() == LATEST_CARD_COUNT) {
        break;
      }
    }

    return selectedAsks;
  }

  private String formatLatestGenerationLabel(User user) {
    List<Activity> activities = user.activities().activities();
    if (activities.isEmpty()) {
      return null;
    }
    Activity latest = activities.stream().max(Comparator.comparingInt(Activity::generation)).orElse(null);
    if (latest == null) {
      return null;
    }
    String partLabel = latest.part() != null ? latest.part().getName() : null;
    return latest.generation() + "기 " + partLabel;
  }

  private Map<Long, User> indexById(List<User> users) {
    return users.stream().collect(Collectors.toMap(User::id, Function.identity(), (existing, replacement) -> existing));
  }

  private int normalizePageSize(Integer size) {
    if (size != null && size >= MIN_PAGE_SIZE && size <= MAX_PAGE_SIZE) {
      return size;
    }
    return DEFAULT_PAGE_SIZE;
  }
}
