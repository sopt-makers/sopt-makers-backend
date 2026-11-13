package org.sopt.makers.domain.crew.dining.port;

import java.util.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.DiningUser;

public interface DiningUserPort {
  Optional<DiningUser> findById(Long id);

  List<DiningUser> findAllById(List<Long> ids);

  default DiningUser findByIdOrThrow(Long id) {
    return findById(id).orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_USER));
  }

  default List<DiningUser> findAllByIdInOrThrow(List<Long> ids) {
    var users = findAllById(ids);
    if (users.size() != new HashSet<>(ids).size())
      throw new DiningException(DiningFailure.NOT_FOUND_USER);
    return users;
  }
}
