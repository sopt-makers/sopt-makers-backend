package org.sopt.makers.domain.app.poke.port;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.app.poke.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendRepositoryPort {

  Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

  Set<Long> findAllFriendIdsByUserId(Long userId);

  List<Friend> findAllByFriendUserId(Long friendUserId);

  List<Friend> findAllByUserId(Long userId);

  List<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendUserIds);

  Page<Friend> findAllByUserIdAndFriendUserIdInOrderByPokeCount(
      Long userId, List<Long> friendUserIds, Pageable pageable);

  Friend save(Friend friend);

  void increasePokeCount(Long friendId);
}
