package org.sopt.makers.storage.db.playground.coffeechat.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.coffeechat.enums.Career;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoffeeChatJpaRepository extends JpaRepository<CoffeeChatEntity, Long> {

  Optional<CoffeeChatEntity> findByMemberId(Long memberId);

  boolean existsByMemberIdAndIsCoffeeChatActivateTrue(Long memberId);

  List<CoffeeChatEntity> findTop12ByIsCoffeeChatActivateTrueOrderByCreatedAtDesc();

  List<CoffeeChatEntity> findAllByIsCoffeeChatActivateTrue();

  List<CoffeeChatEntity> findAllByIsCoffeeChatActivateTrueAndCareer(Career career);

  @Query(
      value =
          "SELECT * FROM coffee_chat WHERE is_coffee_chat_activate = true ORDER BY RAND() LIMIT :limit",
      nativeQuery = true)
  List<CoffeeChatEntity> findRandomActive(@Param("limit") int limit);
}
