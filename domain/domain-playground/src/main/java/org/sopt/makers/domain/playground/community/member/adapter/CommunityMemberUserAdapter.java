package org.sopt.makers.domain.playground.community.member.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.member.port.CommunityMemberPort;
import org.sopt.makers.domain.user.port.PlaygroundCommunityUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityMemberUserAdapter implements CommunityMemberPort {

  private final PlaygroundCommunityUserPort playgroundCommunityUserPort;

  @Override
  public List<MemberInfo> findMemberInfosByIds(List<Long> memberIds) {
    return playgroundCommunityUserPort.getCommunityMemberInfosByIds(memberIds).stream()
        .map(
            info ->
                new MemberInfo(
                    info.id(),
                    info.name(),
                    info.profileImage(),
                    info.activities().stream()
                        .map(
                            activity ->
                                new ActivityInfo(
                                    activity.generation(),
                                    activity.part(),
                                    activity.team(),
                                    activity.isSopt()))
                        .toList(),
                    info.lastCareer() == null
                        ? null
                        : new CareerInfo(
                            info.lastCareer().companyName(), info.lastCareer().title())))
        .toList();
  }
}
