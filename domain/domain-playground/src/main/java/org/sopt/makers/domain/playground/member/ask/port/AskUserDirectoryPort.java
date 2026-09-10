package org.sopt.makers.domain.playground.member.ask.port;

import java.util.List;
import org.sopt.makers.core.type.Part;

/** 파트별로 큐레이션된 질문 대상 멤버 ID 목록을 제공한다. part가 null이면 전체 파트를 반환한다. */
public interface AskUserDirectoryPort {

  List<Long> getAskMemberIds(Part part);
}
