package org.sopt.makers.domain.playground.coffeechat.service;

import static org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatFailure.ALREADY_EXISTS_COFFEE_CHAT;
import static org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatFailure.ALREADY_REVIEWED_COFFEE_CHAT;
import static org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatFailure.COFFEE_CHAT_NOT_FOUND;
import static org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatFailure.COFFEE_CHAT_NOT_REGISTERED;
import static org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatFailure.NOT_PARTICIPATED_COFFEE_CHAT;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.coffeechat.CoffeeChat;
import org.sopt.makers.domain.playground.coffeechat.enums.Career;
import org.sopt.makers.domain.playground.coffeechat.enums.ChatCategory;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatSection;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatTopicType;
import org.sopt.makers.domain.playground.coffeechat.enums.MeetingType;
import org.sopt.makers.domain.playground.coffeechat.exception.CoffeeChatException;
import org.sopt.makers.domain.playground.coffeechat.port.AnonymousProfileImagePort;
import org.sopt.makers.domain.playground.coffeechat.port.AnonymousProfileImagePort.AnonymousImage;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatCachePort;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatHistoryRepositoryPort;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatRepositoryPort;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatRepositoryPort.HistoryInfo;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatRepositoryPort.RecentInfo;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatRepositoryPort.SearchInfo;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatReviewRepositoryPort;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatSmsPort;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatUserPort;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatUserPort.ActivityInfo;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatUserPort.CareerDetail;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatUserPort.UserDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatService {

  private final CoffeeChatRepositoryPort coffeeChatRepositoryPort;
  private final CoffeeChatHistoryRepositoryPort coffeeChatHistoryRepositoryPort;
  private final CoffeeChatReviewRepositoryPort coffeeChatReviewRepositoryPort;
  private final CoffeeChatUserPort coffeeChatUserPort;
  private final CoffeeChatSmsPort coffeeChatSmsPort;
  private final AnonymousProfileImagePort anonymousProfileImagePort;
  private final CoffeeChatCachePort coffeeChatCachePort;
  private final ObjectMapper objectMapper;

  @Transactional
  public void sendCoffeeChatRequest(
      Long receiverId, String senderPhone, ChatCategory category, String content, Long senderId) {
    UserDetail senderDetail = coffeeChatUserPort.getUserDetail(senderId);
    UserDetail receiverDetail = coffeeChatUserPort.getUserDetail(receiverId);
    String senderPart = formatActivitiesAsPartString(senderDetail.activities());
    coffeeChatSmsPort.send(
        senderDetail.name(), senderPart, category, content, senderId, senderPhone,
        receiverDetail.phone());
    coffeeChatHistoryRepositoryPort.save(receiverId, senderId, content);
  }

  public CoffeeChatDetailResult getCoffeeChatDetail(Long memberId, Long detailMemberId) {
    CoffeeChat coffeeChat =
        coffeeChatRepositoryPort
            .findByMemberId(detailMemberId)
            .filter(cc -> cc.isCoffeeChatActivate() || Objects.equals(memberId, detailMemberId))
            .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_FOUND));
    boolean isMine = Objects.equals(memberId, detailMemberId);
    UserDetail userDetail = coffeeChatUserPort.getUserDetail(detailMemberId);
    String organization =
        userDetail.lastCareer().map(CareerDetail::companyName).orElse(userDetail.university());
    String companyJob = userDetail.lastCareer().map(CareerDetail::title).orElse(null);
    return new CoffeeChatDetailResult(
        coffeeChat.coffeeChatBio(),
        detailMemberId,
        userDetail.profileImage(),
        userDetail.name(),
        coffeeChat.career().getTitle(),
        organization,
        companyJob,
        Boolean.TRUE.equals(userDetail.isPhoneBlind()) ? null : userDetail.phone(),
        userDetail.email(),
        coffeeChat.introduction(),
        coffeeChat.sections().stream().map(CoffeeChatSection::getTitle).toList(),
        coffeeChat.coffeeChatTopicTypes().stream().map(CoffeeChatTopicType::getTitle).toList(),
        coffeeChat.topic(),
        coffeeChat.meetingType().getTitle(),
        coffeeChat.guideline(),
        coffeeChat.isCoffeeChatActivate(),
        isMine,
        !coffeeChat.isCoffeeChatActivate());
  }

  public Boolean getCoffeeChatActivate(Long memberId) {
    return coffeeChatRepositoryPort
        .findByMemberId(memberId)
        .map(CoffeeChat::isCoffeeChatActivate)
        .orElse(false);
  }

  @Transactional
  public void updateCoffeeChatOpen(Long memberId, boolean open) {
    CoffeeChat coffeeChat =
        coffeeChatRepositoryPort
            .findByMemberId(memberId)
            .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_REGISTERED));
    coffeeChatRepositoryPort.save(coffeeChat.withActive(open));
  }

  public List<CoffeeChatVoResult> getRecentCoffeeChatList() {
    List<RecentInfo> recentInfoList = coffeeChatRepositoryPort.findRecentCoffeeChatInfo();
    List<Long> memberIds =
        recentInfoList.stream().map(RecentInfo::memberId).distinct().toList();
    Map<Long, UserDetail> userMap = toUserMap(coffeeChatUserPort.getUserDetails(memberIds));
    return recentInfoList.stream()
        .map(
            info -> {
              UserDetail u = userMap.get(info.memberId());
              if (u == null) return null;
              String org = u.lastCareer().map(CareerDetail::companyName).orElse(u.university());
              String job = u.lastCareer().map(CareerDetail::title).orElse(null);
              return new CoffeeChatVoResult(
                  info.memberId(),
                  info.bio(),
                  info.topicTypeList().stream().map(CoffeeChatTopicType::getTitle).toList(),
                  u.profileImage(),
                  u.name(),
                  info.career().getTitle(),
                  org,
                  job,
                  formatActivities(u.activities()),
                  null,
                  null);
            })
        .filter(Objects::nonNull)
        .toList();
  }

  public List<CoffeeChatVoResult> getSearchCoffeeChatList(
      Long memberId, String section, String topicType, String career, String part, String search) {
    CoffeeChatSection sectionEnum = section != null ? CoffeeChatSection.fromTitle(section) : null;
    CoffeeChatTopicType topicTypeEnum =
        topicType != null ? CoffeeChatTopicType.fromTitle(topicType) : null;
    Career careerEnum = career != null ? Career.fromTitle(career) : null;

    List<SearchInfo> results =
        coffeeChatRepositoryPort.findSearchCoffeeChatInfo(memberId, careerEnum);
    results =
        results.stream().filter(distinctByKey(SearchInfo::memberId)).toList();

    List<Long> memberIds = results.stream().map(SearchInfo::memberId).distinct().toList();
    Map<Long, UserDetail> userMap = toUserMap(coffeeChatUserPort.getUserDetails(memberIds));

    if (sectionEnum != null) {
      final CoffeeChatSection finalSection = sectionEnum;
      results =
          results.stream()
              .filter(s -> s.sectionList() != null && s.sectionList().contains(finalSection))
              .toList();
    }
    if (topicTypeEnum != null) {
      final CoffeeChatTopicType finalTopicType = topicTypeEnum;
      results =
          results.stream()
              .filter(s -> s.topicTypeList() != null && s.topicTypeList().contains(finalTopicType))
              .toList();
    }
    if (part != null) {
      results =
          results.stream()
              .filter(
                  s -> {
                    UserDetail u = userMap.get(s.memberId());
                    return u != null
                        && u.activities().stream()
                            .anyMatch(a -> part.equalsIgnoreCase(a.part()));
                  })
              .toList();
    }
    if (search != null && !search.isBlank()) {
      results =
          results.stream()
              .filter(
                  s -> {
                    UserDetail u = userMap.get(s.memberId());
                    boolean universityMatch =
                        u != null && u.university() != null && u.university().contains(search);
                    boolean nameMatch = u != null && u.name().contains(search);
                    boolean careerMatch =
                        u != null
                            && u.lastCareer()
                                .map(c -> c.companyName() != null && c.companyName().contains(search))
                                .orElse(false);
                    return universityMatch || nameMatch || careerMatch;
                  })
              .toList();
    }

    return results.stream()
        .map(
            s -> {
              UserDetail u = userMap.get(s.memberId());
              if (u == null) return null;
              String org = u.lastCareer().map(CareerDetail::companyName).orElse(u.university());
              String job = u.lastCareer().map(CareerDetail::title).orElse(null);
              boolean isMine = Objects.equals(memberId, s.memberId());
              return new CoffeeChatVoResult(
                  s.memberId(),
                  s.bio(),
                  s.topicTypeList().stream().map(CoffeeChatTopicType::getTitle).toList(),
                  u.profileImage(),
                  u.name(),
                  s.career().getTitle(),
                  org,
                  job,
                  formatActivities(u.activities()),
                  isMine,
                  false);
            })
        .filter(Objects::nonNull)
        .toList();
  }

  public List<CoffeeChatHistoryResult> getCoffeeChatHistories(Long memberId) {
    List<HistoryInfo> historyInfoList =
        coffeeChatRepositoryPort.getCoffeeChatHistoryTitles(memberId);
    List<Long> memberIds = historyInfoList.stream().map(HistoryInfo::memberId).toList();
    Map<Long, UserDetail> userMap = toUserMap(coffeeChatUserPort.getUserDetails(memberIds));
    return historyInfoList.stream()
        .map(
            h -> {
              UserDetail u = userMap.get(h.memberId());
              String name = u != null ? u.name() : "";
              return new CoffeeChatHistoryResult(
                  h.id(),
                  h.coffeeChatBio(),
                  name,
                  h.career().getTitle(),
                  h.coffeeChatTopicType().stream().map(CoffeeChatTopicType::getTitle).toList());
            })
        .toList();
  }

  @Transactional
  public void createCoffeeChatDetails(
      Long memberId,
      Career career,
      String introduction,
      List<CoffeeChatSection> sections,
      String bio,
      List<CoffeeChatTopicType> topicTypes,
      String topic,
      MeetingType meetingType,
      String guideline) {
    if (coffeeChatRepositoryPort.existsByMemberIdAndActive(memberId)) {
      throw new CoffeeChatException(ALREADY_EXISTS_COFFEE_CHAT);
    }
    coffeeChatRepositoryPort.save(
        new CoffeeChat(null, memberId, true, career, introduction, sections, bio, topicTypes,
            topic, meetingType, guideline, null, null));
  }

  @Transactional
  public void updateCoffeeChatDetails(
      Long memberId,
      Career career,
      String introduction,
      List<CoffeeChatSection> sections,
      String bio,
      List<CoffeeChatTopicType> topicTypes,
      String topic,
      MeetingType meetingType,
      String guideline) {
    CoffeeChat coffeeChat =
        coffeeChatRepositoryPort
            .findByMemberId(memberId)
            .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_REGISTERED));
    coffeeChatRepositoryPort.save(
        coffeeChat.withInfo(career, introduction, sections, bio, topicTypes, topic, meetingType,
            guideline));
  }

  @Transactional
  public void deleteCoffeeChatDetails(Long memberId) {
    CoffeeChat coffeeChat =
        coffeeChatRepositoryPort
            .findByMemberId(memberId)
            .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_REGISTERED));
    coffeeChatRepositoryPort.delete(coffeeChat.id());
  }

  @Transactional
  public void createCoffeeChatReview(
      Long memberId, Long coffeeChatId, String nickname, String content) {
    CoffeeChat coffeeChat =
        coffeeChatRepositoryPort
            .findById(coffeeChatId)
            .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_FOUND));
    if (!coffeeChatHistoryRepositoryPort.existsByReceiverIdAndSenderId(
        coffeeChat.memberId(), memberId)) {
      throw new CoffeeChatException(NOT_PARTICIPATED_COFFEE_CHAT);
    }
    if (coffeeChatReviewRepositoryPort.existsByReviewerIdAndCoffeeChatId(memberId, coffeeChatId)) {
      throw new CoffeeChatException(ALREADY_REVIEWED_COFFEE_CHAT);
    }
    AnonymousImage image = anonymousProfileImagePort.getRandomImage();
    coffeeChatReviewRepositoryPort.save(memberId, coffeeChatId, image.id(), nickname, content);
  }

  public List<CoffeeChatReviewResult> getRecentCoffeeChatReviews() {
    return coffeeChatReviewRepositoryPort.findTop6ByOrderByIdDesc().stream()
        .map(
            review -> {
              UserDetail u = coffeeChatUserPort.getUserDetail(review.reviewerId());
              CoffeeChat coffeeChat =
                  coffeeChatRepositoryPort.findById(review.coffeeChatId()).orElse(null);
              List<String> topicTypes =
                  coffeeChat != null
                      ? coffeeChat.coffeeChatTopicTypes().stream()
                          .map(CoffeeChatTopicType::getTitle)
                          .toList()
                      : List.of();
              return new CoffeeChatReviewResult(
                  review.anonymousProfileImageUrl(),
                  review.nickname(),
                  formatActivities(u.activities()),
                  topicTypes,
                  review.content());
            })
        .toList();
  }

  public List<RandomCoffeeChatResult> getRandomCoffeeChatList(Long userId) {
    List<RandomCoffeeChatResult> pool;
    try {
      Optional<String> cached = coffeeChatCachePort.getRandomCoffeeChatJson();
      if (cached.isPresent()) {
        pool = objectMapper.readValue(cached.get(), new TypeReference<>() {});
      } else {
        pool = buildRandomCoffeeChatList();
        coffeeChatCachePort.saveRandomCoffeeChatJson(objectMapper.writeValueAsString(pool));
      }
    } catch (Exception e) {
      log.warn("[CoffeeChatService] Redis 캐시 조회 실패, DB fallback 실행", e);
      pool = buildRandomCoffeeChatList();
    }

    UserDetail currentUser = coffeeChatUserPort.getUserDetail(userId);
    String userPart =
        currentUser.activities().stream()
            .max(Comparator.comparingInt(a -> a.isSopt() ? a.generation() * 10 : a.generation()))
            .map(ActivityInfo::part)
            .orElse("");

    List<RandomCoffeeChatResult> samePart =
        pool.stream()
            .filter(r -> r.soptActivities().stream().anyMatch(a -> a.endsWith(" " + userPart)))
            .toList();
    List<RandomCoffeeChatResult> otherPart =
        pool.stream()
            .filter(r -> r.soptActivities().stream().noneMatch(a -> a.endsWith(" " + userPart)))
            .toList();

    List<RandomCoffeeChatResult> result =
        new ArrayList<>(samePart.subList(0, Math.min(samePart.size(), 4)));
    if (result.size() < 4) {
      result.addAll(otherPart.subList(0, Math.min(otherPart.size(), 4 - result.size())));
    }
    return result;
  }

  private List<RandomCoffeeChatResult> buildRandomCoffeeChatList() {
    return coffeeChatRepositoryPort.findRandomActiveCoffeeChats(20).stream()
        .map(
            cc -> {
              UserDetail u = coffeeChatUserPort.getUserDetail(cc.memberId());
              List<String> activities = formatActivities(u.activities());
              String org = u.lastCareer().map(CareerDetail::companyName).orElse(u.university());
              String job = u.lastCareer().map(CareerDetail::title).orElse(null);
              return new RandomCoffeeChatResult(
                  cc.memberId(),
                  cc.coffeeChatBio(),
                  u.profileImage(),
                  u.name(),
                  cc.career().getTitle(),
                  org,
                  job,
                  activities,
                  cc.coffeeChatTopicTypes().stream().map(CoffeeChatTopicType::getTitle).toList());
            })
        .toList();
  }

  private List<String> formatActivities(List<ActivityInfo> activities) {
    return activities.stream()
        .sorted(
            Comparator.comparingInt(
                (ActivityInfo a) -> -(a.isSopt() ? a.generation() * 10 : a.generation())))
        .map(
            a ->
                a.isSopt()
                    ? String.format("%d기 %s", a.generation(), a.part())
                    : String.format("%d기 메이커스", a.generation()))
        .toList();
  }

  private String formatActivitiesAsPartString(List<ActivityInfo> activities) {
    return formatActivities(activities).stream().collect(Collectors.joining(", "));
  }

  private Map<Long, UserDetail> toUserMap(List<UserDetail> details) {
    return details.stream()
        .collect(Collectors.toMap(UserDetail::id, Function.identity(), (a, b) -> a));
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  // --- Result records (returned to controller) ---

  public record CoffeeChatVoResult(
      Long memberId,
      String bio,
      List<String> topicTypeList,
      String profileImage,
      String name,
      String career,
      String organization,
      String companyJob,
      List<String> soptActivities,
      Boolean isMine,
      Boolean isBlind) {}

  public record CoffeeChatDetailResult(
      String bio,
      Long memberId,
      String profileImage,
      String name,
      String career,
      String organization,
      String companyJob,
      String phone,
      String email,
      String introduction,
      List<String> sections,
      List<String> topicTypeList,
      String topic,
      String meetingType,
      String guideline,
      Boolean isCoffeeChatActivate,
      Boolean isMine,
      Boolean isBlind) {}

  public record CoffeeChatHistoryResult(
      Long id,
      String coffeeChatBio,
      String name,
      String career,
      List<String> coffeeChatTopicType) {}

  public record CoffeeChatReviewResult(
      String profileImage,
      String nickname,
      List<String> soptActivities,
      List<String> coffeeChatTopicType,
      String content) {}

  public record RandomCoffeeChatResult(
      Long memberId,
      String coffeeChatBio,
      String profileImage,
      String name,
      String career,
      String organization,
      String companyJob,
      List<String> soptActivities,
      List<String> topicTypeList) {}
}
