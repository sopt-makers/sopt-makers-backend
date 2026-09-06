package org.sopt.makers.storage.db.playground.coffeechat.repository;

import java.util.Collection;
import java.util.List;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeChatJpaRepository extends JpaRepository<CoffeeChatEntity, Long> {

  boolean existsByMemberIdAndIsCoffeeChatActivateTrue(Long memberId);

  boolean existsByMemberId(Long memberId);

  List<CoffeeChatEntity> findAllByMemberIdInAndIsCoffeeChatActivateTrue(Collection<Long> memberIds);
}
