package org.sopt.makers.clients.notification;

import java.util.List;
import java.util.Map;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.playground.member.ask.port.AskUserDirectoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 파트별 질문 대상 멤버 큐레이션 목록. 운영 환경 여부에 따라 노출 대상을 분리한다(레거시 동작 보존). */
@Component
public class AskUserDirectoryAdapter implements AskUserDirectoryPort {

  private static final Map<Part, List<Long>> DEV_ASK_MEMBERS =
      Map.of(
          Part.SERVER, List.of(929L, 209L),
          Part.IOS, List.of(192L),
          Part.ANDROID, List.of(223L),
          Part.WEB, List.of(945L),
          Part.DESIGN, List.of(930L),
          Part.PLAN, List.of(229L));

  private static final Map<Part, List<Long>> PROD_ASK_MEMBERS =
      Map.of(
          Part.PLAN, List.of(319L, 11L, 661L, 769L),
          Part.DESIGN, List.of(144L, 358L, 64L, 210L),
          Part.WEB, List.of(84L, 85L, 860L, 784L, 673L, 574L, 115L, 635L, 858L),
          Part.IOS, List.of(35L, 45L, 22L),
          Part.ANDROID, List.of(585L, 21L, 407L),
          Part.SERVER, List.of(647L, 989L, 306L, 60L, 221L, 293L));

  private final String activeProfile;

  public AskUserDirectoryAdapter(@Value("${spring.profiles.active:}") String activeProfile) {
    this.activeProfile = activeProfile;
  }

  @Override
  public List<Long> getAskMemberIds(Part part) {
    Map<Part, List<Long>> table = "prod".equals(activeProfile) ? PROD_ASK_MEMBERS : DEV_ASK_MEMBERS;
    if (part == null) {
      return table.values().stream().flatMap(List::stream).toList();
    }
    return table.getOrDefault(part, List.of());
  }
}
