package org.sopt.makers.storage.db.app.poke.adapter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.Friend;
import org.sopt.makers.domain.app.poke.port.FriendRepositoryPort;
import org.sopt.makers.storage.db.app.poke.entity.FriendEntity;
import org.sopt.makers.storage.db.app.poke.repository.FriendJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendRepositoryAdapter implements FriendRepositoryPort {

  private final FriendJpaRepository friendJpaRepository;

  @Override
  public Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId) {
    return friendJpaRepository
        .findByUserIdAndFriendUserId(userId, friendUserId)
        .map(FriendEntity::toDomain);
  }

  @Override
  public Set<Long> findAllFriendIdsByUserId(Long userId) {
    return friendJpaRepository.findAllFriendIdsByUserId(userId);
  }

  @Override
  public List<Friend> findAllByFriendUserId(Long friendUserId) {
    return toDomains(friendJpaRepository.findAllByFriendUserId(friendUserId));
  }

  @Override
  public List<Friend> findAllByUserId(Long userId) {
    return toDomains(friendJpaRepository.findAllByUserId(userId));
  }

  @Override
  public List<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendUserIds) {
    return toDomains(friendJpaRepository.findAllByUserIdAndFriendUserIdIn(userId, friendUserIds));
  }

  @Override
  @Transactional
  public Friend save(Friend friend) {
    return friendJpaRepository.save(FriendEntity.from(friend)).toDomain();
  }

  @Override
  @Transactional
  public void increasePokeCount(Long friendId) {
    friendJpaRepository.increasePokeCount(friendId);
  }

  private List<Friend> toDomains(List<FriendEntity> entities) {
    return entities.stream().map(FriendEntity::toDomain).toList();
  }
}
