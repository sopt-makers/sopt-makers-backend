package org.sopt.makers.domain.crew.soptmap.port;

import java.util.Optional;
import org.sopt.makers.domain.crew.soptmap.SoptMapEventPolicy;

public interface SoptMapEventPolicyPort {

  Optional<SoptMapEventPolicy> findPolicy();
}
