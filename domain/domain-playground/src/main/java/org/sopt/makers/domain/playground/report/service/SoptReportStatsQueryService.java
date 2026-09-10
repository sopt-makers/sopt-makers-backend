package org.sopt.makers.domain.playground.report.service;

import static org.sopt.makers.domain.playground.report.EventData.COFFEE_CHAT_TAB_VISIT_COUNT;
import static org.sopt.makers.domain.playground.report.EventData.CREW_TAB_VISIT_COUNT;
import static org.sopt.makers.domain.playground.report.EventData.MEMBER_PROFILE_CARD_VIEW_COUNT;
import static org.sopt.makers.domain.playground.report.EventData.MEMBER_TAB_VISIT_COUNT;
import static org.sopt.makers.domain.playground.report.EventData.PROJECT_TAB_VISIT_COUNT;
import static org.sopt.makers.domain.playground.report.EventData.TOTAL_VISIT_COUNT;
import static org.sopt.makers.domain.playground.report.util.ReportJsonDataSerializer.serialize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.comment.port.CommentRepositoryPort;
import org.sopt.makers.domain.playground.community.post.port.PostLikeRepositoryPort;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.report.PlaygroundType;
import org.sopt.makers.domain.playground.report.PlaygroundTypeStats;
import org.sopt.makers.domain.playground.report.SoptReportCategory;
import org.sopt.makers.domain.playground.report.SoptReportStats;
import org.sopt.makers.domain.playground.report.port.AmplitudeEventStatsPort;
import org.sopt.makers.domain.playground.report.port.CrewReportClientPort;
import org.sopt.makers.domain.playground.report.port.SoptReportStatsRepositoryPort;
import org.sopt.makers.domain.playground.report.port.WordChainGameStatsPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptReportStatsQueryService {

  private static final String TYPE_COMMON_SOPT_REPORT_STATS = "commonSoptReportStats";
  private static final String TYPE_MY_SOPT_REPORT_STATS = "mySoptReportStats";
  private static final Integer REPORT_FILTER_YEAR = 2024;
  private static final LocalDateTime START_DATE_OF_YEAR =
      LocalDateTime.of(REPORT_FILTER_YEAR, 1, 1, 0, 0);
  private static final LocalDateTime END_DATE_OF_YEAR =
      LocalDateTime.of(REPORT_FILTER_YEAR, 12, 31, 23, 59);
  private static final Integer CREW_TOP_FASTEST_JOINED_GROUP_LIMIT = 3;
  private static final int MAX_WORD_LIST_SIZE = 6;
  private static final String COFFEE_CHAT_QUERY_START_DATE = "2024-11-03";
  private static final String COFFEE_CHAT_QUERY_END_DATE = "2024-12-31";

  private final SoptReportStatsRepositoryPort soptReportStatsRepositoryPort;
  private final AmplitudeEventStatsPort amplitudeEventStatsPort;
  private final PostRepositoryPort postRepositoryPort;
  private final PostLikeRepositoryPort postLikeRepositoryPort;
  private final CommentRepositoryPort commentRepositoryPort;
  private final WordChainGameStatsPort wordChainGameStatsPort;
  private final CrewReportClientPort crewReportClientPort;

  @Cacheable(cacheNames = TYPE_COMMON_SOPT_REPORT_STATS, key = "#category")
  public Map<String, Object> getSoptReportStats(SoptReportCategory category) {
    return soptReportStatsRepositoryPort.findByCategory(category.name()).stream()
        .collect(
            Collectors.toMap(
                SoptReportStats::templateKey,
                stats -> Objects.requireNonNull(serialize(stats.data()))));
  }

  @Cacheable(cacheNames = TYPE_MY_SOPT_REPORT_STATS, key = "#memberId")
  public MySoptReportStatsResult getMySoptReportStats(Long memberId) {
    long totalVisitCount =
        amplitudeEventStatsPort.countAllByUserIdAndEventTypeAndEventTimeContains(
            memberId.toString(), TOTAL_VISIT_COUNT.getProperty(), REPORT_FILTER_YEAR.toString());

    int likeCount =
        postLikeRepositoryPort.countAllByUserIdAndCreatedAtBetween(
            memberId, START_DATE_OF_YEAR, END_DATE_OF_YEAR);

    List<String> memberWords =
        wordChainGameStatsPort.findWordsByMemberIdAndCreatedAtBetween(
            memberId, START_DATE_OF_YEAR, END_DATE_OF_YEAR);
    List<String> wordList = getShuffledWordList(memberWords);
    int playCount = memberWords.size();
    int winCount =
        (int)
            wordChainGameStatsPort.countWinnersByUserIdAndCreatedAtBetween(
                memberId, START_DATE_OF_YEAR, END_DATE_OF_YEAR);

    long viewCount =
        amplitudeEventStatsPort.countAllByUserIdAndEventTypeAndEventTimeContains(
            memberId.toString(),
            MEMBER_PROFILE_CARD_VIEW_COUNT.getProperty(),
            REPORT_FILTER_YEAR.toString());

    List<String> topFastestJoinedGroupList =
        crewReportClientPort.getFastestAppliedGroupTitles(
            memberId, CREW_TOP_FASTEST_JOINED_GROUP_LIMIT, REPORT_FILTER_YEAR);

    return new MySoptReportStatsResult(
        determinePlaygroundType(memberId, totalVisitCount, likeCount, playCount).getTitle(),
        totalVisitCount,
        likeCount,
        viewCount,
        topFastestJoinedGroupList,
        playCount,
        winCount,
        wordList);
  }

  private List<String> getShuffledWordList(List<String> memberWords) {
    List<String> wordList = new ArrayList<>(memberWords);
    Collections.shuffle(wordList);
    return wordList.size() > MAX_WORD_LIST_SIZE
        ? wordList.subList(0, MAX_WORD_LIST_SIZE)
        : wordList;
  }

  private PlaygroundType determinePlaygroundType(
      Long memberId, long totalVisitCount, int likeCount, int wordChainGamePlayCount) {
    if (totalVisitCount == 0) {
      return PlaygroundType.DEFAULT;
    }

    int postCount =
        postRepositoryPort.countAllByWriterIdAndCreatedAtBetween(
            memberId, START_DATE_OF_YEAR, END_DATE_OF_YEAR);
    int commentCount =
        commentRepositoryPort.countAllByWriterIdAndCreatedAtBetween(
            memberId, START_DATE_OF_YEAR, END_DATE_OF_YEAR);

    long memberVisitCount =
        amplitudeEventStatsPort.countByUserIdAndEventTypeAndPagePathAndEventTimeContains(
            memberId.toString(),
            MEMBER_TAB_VISIT_COUNT.getProperty(),
            MEMBER_TAB_VISIT_COUNT.getPagePath(),
            REPORT_FILTER_YEAR.toString());
    long projectVisitCount =
        amplitudeEventStatsPort.countByUserIdAndEventTypeAndPagePathAndEventTimeContains(
            memberId.toString(),
            PROJECT_TAB_VISIT_COUNT.getProperty(),
            PROJECT_TAB_VISIT_COUNT.getPagePath(),
            REPORT_FILTER_YEAR.toString());
    long crewVisitCount =
        amplitudeEventStatsPort.countByUserIdAndEventTypeAndPagePathAndEventTimeContains(
            memberId.toString(),
            CREW_TAB_VISIT_COUNT.getProperty(),
            CREW_TAB_VISIT_COUNT.getPagePath(),
            REPORT_FILTER_YEAR.toString());

    long coffeeChatVisitCount =
        amplitudeEventStatsPort.countByUserIdAndEventTypeAndPagePathAndEventTimeBetween(
            memberId.toString(),
            COFFEE_CHAT_TAB_VISIT_COUNT.getProperty(),
            COFFEE_CHAT_TAB_VISIT_COUNT.getPagePath(),
            COFFEE_CHAT_QUERY_START_DATE,
            COFFEE_CHAT_QUERY_END_DATE);
    long totalVisitCountForCoffeeChat =
        amplitudeEventStatsPort.countAllByUserIdAndEventTypeAndEventTimeBetween(
            memberId.toString(),
            TOTAL_VISIT_COUNT.getProperty(),
            COFFEE_CHAT_QUERY_START_DATE,
            COFFEE_CHAT_QUERY_END_DATE);

    double coffeeChatStats;
    if (totalVisitCountForCoffeeChat == 0) {
      coffeeChatStats = 0;
    } else {
      coffeeChatStats = ((double) coffeeChatVisitCount / totalVisitCountForCoffeeChat) * 100;
    }

    return new PlaygroundTypeStats(
            ((double) (postCount + commentCount + likeCount) / totalVisitCount) * 100,
            ((double) memberVisitCount / totalVisitCount) * 100,
            ((double) projectVisitCount / totalVisitCount) * 100,
            ((double) wordChainGamePlayCount / totalVisitCount) * 100,
            coffeeChatStats,
            ((double) crewVisitCount / totalVisitCount) * 100)
        .getTopStats();
  }

  public record MySoptReportStatsResult(
      String myType,
      Long totalVisitCount,
      Integer likeCount,
      Long viewCount,
      List<String> topFastestJoinedGroupList,
      Integer playCount,
      Integer winCount,
      List<String> wordList)
      implements java.io.Serializable {}
}
