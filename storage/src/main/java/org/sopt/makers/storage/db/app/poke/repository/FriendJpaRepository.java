package org.sopt.makers.storage.db.app.poke.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.storage.db.app.poke.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendJpaRepository extends JpaRepository<FriendEntity, Long> {

  Optional<FriendEntity> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

  @Query("SELECT f.friendUserId FROM FriendEntity f WHERE f.userId = :userId")
  Set<Long> findAllFriendIdsByUserId(@Param("userId") Long userId);

  List<FriendEntity> findAllByFriendUserId(Long friendUserId);

  List<FriendEntity> findAllByUserId(Long userId);

  List<FriendEntity> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendUserIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE FriendEntity f SET f.pokeCount = f.pokeCount + 1, f.updatedAt = CURRENT_TIMESTAMP"
          + " WHERE f.id = :friendId")
  void increasePokeCount(@Param("friendId") Long friendId);
}
