package org.sopt.makers.storage.db.playground.coffeechat.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.CoffeeChat;
import org.sopt.makers.domain.playground.coffeechat.enums.Career;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatRepositoryPort;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatEntity;
import org.sopt.makers.storage.db.playground.coffeechat.repository.CoffeeChatJpaRepository;
import org.sopt.makers.storage.db.playground.coffeechat.repository.CoffeeChatQueryRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatRepositoryAdapter implements CoffeeChatRepositoryPort {

  private final CoffeeChatJpaRepository coffeeChatJpaRepository;
  private final CoffeeChatQueryRepository coffeeChatQueryRepository;

  @Transactional
  @Override
  public CoffeeChat save(CoffeeChat coffeeChat) {
    return coffeeChatJpaRepository.save(CoffeeChatEntity.from(coffeeChat)).toDomain();
  }

  @Override
  public Optional<CoffeeChat> findById(Long id) {
    return coffeeChatJpaRepository.findById(id).map(CoffeeChatEntity::toDomain);
  }

  @Override
  public Optional<CoffeeChat> findByMemberId(Long memberId) {
    return coffeeChatJpaRepository.findByMemberId(memberId).map(CoffeeChatEntity::toDomain);
  }

  @Override
  public boolean existsByMemberIdAndActive(Long memberId) {
    return coffeeChatJpaRepository.existsByMemberIdAndIsCoffeeChatActivateTrue(memberId);
  }

  @Transactional
  @Override
  public void delete(Long id) {
    coffeeChatJpaRepository.deleteById(id);
  }

  @Override
  public List<RecentInfo> findRecentCoffeeChatInfo() {
    return coffeeChatJpaRepository
        .findTop12ByIsCoffeeChatActivateTrueOrderByCreatedAtDesc()
        .stream()
        .map(
            e ->
                new RecentInfo(
                    e.getMemberId(),
                    e.getCoffeeChatBio(),
                    e.getCoffeeChatTopicTypes(),
                    e.getCareer()))
        .toList();
  }

  @Override
  public List<SearchInfo> findSearchCoffeeChatInfo(Long memberId, Career career) {
    List<CoffeeChatEntity> entities =
        career == null
            ? coffeeChatJpaRepository.findAllByIsCoffeeChatActivateTrue()
            : coffeeChatJpaRepository.findAllByIsCoffeeChatActivateTrueAndCareer(career);
    return entities.stream()
        .map(
            e ->
                new SearchInfo(
                    e.getMemberId(),
                    e.getCoffeeChatBio(),
                    e.getSections(),
                    e.getCoffeeChatTopicTypes(),
                    e.getCareer()))
        .toList();
  }

  @Override
  public List<HistoryInfo> getCoffeeChatHistoryTitles(Long memberId) {
    return coffeeChatQueryRepository.findCoffeeChatHistoryTitles(memberId);
  }

  @Override
  public List<CoffeeChat> findRandomActiveCoffeeChats(int limit) {
    return coffeeChatJpaRepository.findRandomActive(limit).stream()
        .map(CoffeeChatEntity::toDomain)
        .toList();
  }
}
