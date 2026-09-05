package org.sopt.makers.domain.app.soptamp.port;

import java.util.Map;
import org.sopt.makers.core.type.Part;

public interface SoptampPartMemberCountPort {

  Map<Part, Long> countMembersByPart(int generation);
}
