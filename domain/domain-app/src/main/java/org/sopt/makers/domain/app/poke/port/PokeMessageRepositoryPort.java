package org.sopt.makers.domain.app.poke.port;

import java.util.List;
import org.sopt.makers.domain.app.poke.PokeMessage;
import org.sopt.makers.domain.app.poke.PokeMessageType;

public interface PokeMessageRepositoryPort {

  List<PokeMessage> findAllByType(PokeMessageType type);
}
