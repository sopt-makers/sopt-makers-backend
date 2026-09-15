package org.sopt.makers.domain.app.poke.port;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.app.poke.Friend;

public interface FriendRepositoryPort {

  Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

  Set<Long> findAllFriendIdsByUserId(Long userId);

  List<Friend> findAllByFriendUserId(Long friendUserId);

  List<Friend> findAllByUserId(Long userId);

  List<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendUserIds);

  Friend save(Friend friend);

  void increasePokeCount(Long friendId);
}
