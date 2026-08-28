package org.sopt.makers.domain.playground.coffeechat.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.coffeechat.CoffeeChat;
import org.sopt.makers.domain.playground.coffeechat.enums.Career;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatSection;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatTopicType;

public interface CoffeeChatRepositoryPort {

  CoffeeChat save(CoffeeChat coffeeChat);

  Optional<CoffeeChat> findById(Long id);

  Optional<CoffeeChat> findByMemberId(Long memberId);

  boolean existsByMemberIdAndActive(Long memberId);

  void delete(Long id);

  List<RecentInfo> findRecentCoffeeChatInfo();

  List<SearchInfo> findSearchCoffeeChatInfo(Long memberId, Career career);

  List<HistoryInfo> getCoffeeChatHistoryTitles(Long memberId);

  List<CoffeeChat> findRandomActiveCoffeeChats(int limit);

  record RecentInfo(Long memberId, String bio, List<CoffeeChatTopicType> topicTypeList, Career career) {}

  record SearchInfo(
      Long memberId,
      String bio,
      List<CoffeeChatSection> sectionList,
      List<CoffeeChatTopicType> topicTypeList,
      Career career) {}

  record HistoryInfo(
      Long id,
      String coffeeChatBio,
      Long memberId,
      Career career,
      List<CoffeeChatTopicType> coffeeChatTopicType) {}
}
