package org.sopt.makers.domain.playground.member.tl.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.tl.TlUser;

public interface TlUserRepositoryPort {

  TlUser save(TlUser tlUser);

  Optional<TlUser> findById(Long id);

  void deleteById(Long id);
}
